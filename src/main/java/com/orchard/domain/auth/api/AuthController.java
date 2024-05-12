package com.orchard.domain.auth.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.orchard.domain.auth.application.MemberAuthService;
import com.orchard.domain.auth.dto.LoginRequestDto;
import com.orchard.global.common.TokenDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class AuthController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {
        return new ResponseEntity<>(
                memberAuthService.authorize(requestDto.getEmail(), requestDto.getPassword()), HttpStatus.OK);
    }

    /*@PostMapping("/reissue")
    public ResponseEntity<TokenDTO> reissue(@Valid @RequestBody TokenDTO requestToken) {
        TokenDTO tokenDTO =
                memberAuthService.reissue(requestToken.getRefreshToken());

        return ResponseEntity.ok(tokenDTO);
    }*/

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenDTO tokenDTO) {
        memberAuthService.logout(tokenDTO.getAccessToken());
        return ResponseEntity.ok().build();
    }
}
