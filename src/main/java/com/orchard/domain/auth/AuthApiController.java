package com.orchard.domain.auth;

import com.orchard.domain.auth.dto.LoginRequestDto;
import com.orchard.global.common.RefreshToken;
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

@RestController
@RequestMapping("/api/v1/member")
public class AuthApiController {

    private final Long refreshTokenExpiration;
    private final MemberAuthService memberAuthService;
    public AuthApiController(MemberAuthService memberAuthService, @Value("${jwt.refreshToken-validity-in-seconds}") String refreshTokenExpiration) {
        this.refreshTokenExpiration = Long.valueOf(refreshTokenExpiration);
        this.memberAuthService = memberAuthService;
    }
    @PostMapping("/login")
//    @CrossOrigin(origins = "https://kingcherry.store", allowCredentials = "true")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginRequestDto requestDto) {
        TokenDTO tokenDTO = memberAuthService.authorize(requestDto.getEmail(), requestDto.getPassword());

        ResponseCookie responseCookie = refreshCookie(tokenDTO.getRefreshToken().refreshToken(), refreshTokenExpiration);
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Credentials", "true")
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(tokenDTO);
    }

    @PostMapping("/reissue")
//    @CrossOrigin(originPatterns = "https://kingcherry.store", allowCredentials = "true")
    public ResponseEntity<TokenDTO> reissue(@CookieValue("AUTH") String refreshToken) {

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
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenDTO tokenDTO) {
        memberAuthService.logout(tokenDTO.getAccessToken());
        return ResponseEntity.ok().build();
    }

    private ResponseCookie refreshCookie(String refreshToken, Long maxAge) {
        return ResponseCookie.from("AUTH", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true) // 운영시 주석 해제
                .maxAge(maxAge)
                .domain("kingcherry.store")
                .sameSite(Cookie.SameSite.STRICT.name())
                .build();
    }
}
