package com.orchard.domain.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.domain.vo.UserPassword;
import com.orchard.global.common.AccessToken;
import com.orchard.global.common.RefreshToken;
import com.orchard.global.common.TokenDTO;
import com.orchard.global.common.TokenProvider;
import com.orchard.global.error.exception.ErrorCode;
import com.orchard.global.jwt.error.TokenNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberAuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;
//    private final RedisService redisService;

    // 로그인
    public TokenDTO authorize(final UserEmail userEmail, final UserPassword userPassword) {
        final String email = userEmail.userEmail();
        final String password = userPassword.userPassword();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = managerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("authentication.principal : {}", authentication.getPrincipal());
        log.debug("authentication.authorities : {}", authentication.getAuthorities());

        return tokenProvider.createToken(authentication.getName(), authentication);
    }

    // 토큰 재발급
    public TokenDTO reissue(AccessToken accessToken, RefreshToken refreshToken) {
        if (!tokenProvider.validateToken(refreshToken.refreshToken())) {
            throw new TokenNotFoundException(ErrorCode.Token_NOT_FOUND);
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken.accessToken());

        UserDetails principal = (UserDetails) authentication.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(principal.getUsername(), authentication);
    }

    public void logout(RefreshToken refreshToken, AccessToken accessToken) {
//        redisService.setBlackList(accessToken.accessToken(), "accessToken", 3600);
//        redisService.setBlackList(refreshToken.refreshToken(), "refreshToken", 1209600);
    }
}
