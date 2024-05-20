package com.orchard.domain.auth.application;

import com.orchard.domain.auth.domain.persist.Message;
import com.orchard.domain.auth.domain.persist.MessageRepository;
import com.orchard.domain.auth.domain.persist.RefreshTokenEntity;
import com.orchard.domain.auth.domain.persist.RefreshTokenRepository;
import com.orchard.domain.auth.dto.SmsRequestDto;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.persist.MemberRepository;
import com.orchard.global.common.*;
import com.orchard.global.jwt.error.TokenNotFoundException;
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
import com.orchard.global.error.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberAuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;
    private final RefreshTokenRepository tokenRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    // 로그인
    public TokenDTO authorize(final UserEmail userEmail, final UserPassword userPassword) {
        final String email = userEmail.userEmail();
        final String password = userPassword.userPassword();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        Optional<Member> member = memberRepository.findByEmail(UserEmail.from(email));
        member.ifPresent(value -> messageRepository.deleteByPhoneNumber(value.getPhoneNumber()));

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

        String role = authentication.getAuthorities().stream()
                .findFirst().get().getAuthority();

        UserDetails principal = (UserDetails) authentication.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenDTO tokenDTO = tokenProvider.createToken(principal.getUsername(), authentication);
        tokenDTO.setOriginRefreshToken(refreshToken);
        tokenDTO.setRole(role);

        return tokenDTO;
    }

    public void logout(AccessToken accessToken) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken.accessToken());
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Optional<RefreshTokenEntity> refreshTokenEntity = tokenRepository.findByEmail(UserEmail.from(principal.getUsername()));
        refreshTokenEntity.ifPresent(tokenRepository::delete);
    }

    public ResultResponseDto checkCode(SmsRequestDto smsRequestDto) {
        Optional<Message> message = messageRepository.findByPhoneNumber(smsRequestDto.getPhoneNumber());
        ResultResponseDto resultResponseDto = new ResultResponseDto();
        if(message.isEmpty()) {
            resultResponseDto.setCode("500");
            resultResponseDto.setMsg("코드가 존재하지 않습니다.");
        } else if(message.get().getCode().equals(smsRequestDto.getCode())) {
            if(message.get().getUpdateTime().plusMinutes(3).isBefore(LocalDateTime.now())) {
                resultResponseDto.setCode("500");
                resultResponseDto.setMsg("만료된 코드 입니다.");
            } else {
                resultResponseDto.setCode("200");
                resultResponseDto.setMsg("코드가 일치합니다.");
            }
        } else {
            resultResponseDto.setCode("500");
            resultResponseDto.setMsg("코드가 일치하지 않습니다.");
        }
        return resultResponseDto;
    }
}
