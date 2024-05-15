package com.orchard.domain.auth;

import com.orchard.domain.auth.dto.LoginRequestDto;
import com.orchard.domain.auth.dto.SmsRequestDto;
import com.orchard.domain.ncp.NcpService;
import com.orchard.global.common.RefreshToken;
import com.orchard.global.common.ResultResponseDto;
import com.orchard.global.jwt.error.TokenNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.orchard.domain.auth.application.MemberAuthService;
import com.orchard.global.common.TokenDTO;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {
    private final NcpService ncpService;
    private final Long refreshTokenExpiration;
    private final MemberAuthService memberAuthService;

    private final String secure;
    private final String domain;

    public AuthApiController(NcpService ncpService,
                             MemberAuthService memberAuthService,
                             @Value("${jwt.refreshToken-validity-in-seconds}") String refreshTokenExpiration,
                             @Value("${cookie.secure}") String secure,
                             @Value("${cookie.domain}") String domain
    ) {
        this.refreshTokenExpiration = Long.valueOf(refreshTokenExpiration);
        this.memberAuthService = memberAuthService;
        this.ncpService = ncpService;
        this.secure = secure;
        this.domain = domain;
    }

    @PostMapping("/code")
    public ResponseEntity<ResultResponseDto> code(@Valid @RequestBody SmsRequestDto smsRequestDto) {
        ResultResponseDto resultResponseDto = memberAuthService.checkCode(smsRequestDto);
        return ResponseEntity.ok(resultResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginRequestDto requestDto) {
        TokenDTO tokenDTO = memberAuthService.authorize(requestDto.getEmail(), requestDto.getPassword());

        ResponseCookie responseCookie = refreshCookie(tokenDTO.getRefreshToken().refreshToken(), 30*60L);
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Credentials", "true")
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(tokenDTO);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDTO> reissue(@CookieValue(value = "AUTH", required = false) String refreshToken) {

        TokenDTO tokenDTO;
        ResponseEntity<TokenDTO> response;
        try {
            tokenDTO = memberAuthService.reissue(RefreshToken.from(refreshToken));
            response = ResponseEntity.ok(tokenDTO);
        } catch (TokenNotFoundException e) {
            ResponseCookie responseCookie = refreshCookie(refreshToken, 0L);
            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
        return response;
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenDTO tokenDTO, @CookieValue(value = "AUTH") String refreshToken) {
        memberAuthService.logout(tokenDTO.getAccessToken());
        ResponseCookie responseCookie = refreshCookie(refreshToken, 0L);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @PostMapping("/phone")
    public ResponseEntity<Map<String, String>> send(@Valid @RequestBody SmsRequestDto smsRequestDto) {
        return ResponseEntity.ok(ncpService.sendMessage(smsRequestDto.getPhoneNumber().userPhoneNumber()));
    }

    private ResponseCookie refreshCookie(String refreshToken, Long maxAge) {
        return ResponseCookie.from("AUTH", refreshToken)
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge)
                .secure(Boolean.parseBoolean(secure))
                .domain(domain)
                .sameSite(Cookie.SameSite.STRICT.name())
                .build();
    }
}
