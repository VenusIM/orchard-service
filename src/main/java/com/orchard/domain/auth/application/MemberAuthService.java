package com.orchard.domain.auth.application;

import com.orchard.domain.auth.domain.persist.RefreshTokenEntity;
import com.orchard.domain.auth.domain.persist.RefreshTokenRepository;
import com.orchard.global.common.RefreshToken;
import com.orchard.global.jwt.error.TokenNotFoundException;
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
import com.orchard.global.common.TokenDTO;
import com.orchard.global.common.TokenProvider;
import com.orchard.global.error.exception.ErrorCode;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class MemberAuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;
    private final RefreshTokenRepository tokenRepository;

    public MemberAuthService(TokenProvider tokenProvider, AuthenticationManagerBuilder managerBuilder, RefreshTokenRepository tokenRepository) {
        this.tokenProvider = tokenProvider;
        this.managerBuilder = managerBuilder;
        this.tokenRepository = tokenRepository;

    }

    // 로그인
    public TokenDTO authorize(final UserEmail userEmail, final UserPassword userPassword) {
        final String email = userEmail.userEmail();
        final String password = userPassword.userPassword();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = managerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDTO tokenDTO = tokenProvider.createToken(authentication.getName(), authentication);

        Optional<RefreshTokenEntity> optionalRefreshToken = tokenRepository.findByEmail(userEmail);

        if(optionalRefreshToken.isEmpty()) {
            RefreshTokenEntity token = new RefreshTokenEntity(tokenDTO.getRefreshToken(), UserEmail.from(email));
            tokenRepository.save(token);
        } else if(!tokenProvider.validateToken(optionalRefreshToken.get().getRefreshToken().refreshToken())) {
            tokenRepository.save(new RefreshTokenEntity(tokenDTO.getRefreshToken(), UserEmail.from(email)));
        } else {
            tokenDTO.setOriginRefreshToken(optionalRefreshToken.get().getRefreshToken());
        }

        log.debug("authentication.principal : {}", authentication.getPrincipal());
        log.debug("authentication.authorities : {}", authentication.getAuthorities());

        return tokenDTO;
    }

    // 토큰 재발급
    public TokenDTO reissue(RefreshToken refreshToken) {

        if (!tokenProvider.validateToken(refreshToken.refreshToken())) {
            throw new TokenNotFoundException(ErrorCode.Token_NOT_FOUND);
        }

        Authentication authentication = tokenProvider.getAuthentication(refreshToken.refreshToken());

        UserDetails principal = (UserDetails) authentication.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenDTO tokenDTO = tokenProvider.createToken(principal.getUsername(), authentication);
        tokenDTO.setOriginRefreshToken(refreshToken);

        return tokenDTO;
    }

    public void logout(AccessToken accessToken) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken.accessToken());
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Optional<RefreshTokenEntity> refreshTokenEntity = tokenRepository.findByEmail(UserEmail.from(principal.getUsername()));
        refreshTokenEntity.ifPresent(tokenRepository::delete);
    }
}
