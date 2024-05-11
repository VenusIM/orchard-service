package com.orchard.global.jwt;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import com.orchard.global.common.TokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest servletRequest, @NotNull HttpServletResponse servletResponse, @NotNull FilterChain filterChain) throws ServletException, IOException {

        log.debug("request : {}", servletRequest);

        String jwt = resolveToken(servletRequest);

        log.debug("jwt : {}", jwt);

        String requestURI = servletRequest.getRequestURI();

        log.info("doFilter 들어옴");

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("{} 인정 정보 저장, uri : {}", authentication.getName(), requestURI);
        } else {
            log.info("유효한 JWT 토큰이 없습니다. uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String[] excludePath = {
                "/",
                "/assets/",
                "/js/",
                "/css/",
                "/fontawesome/"
        };

        return Arrays.stream(excludePath)
                .anyMatch(request.getRequestURI()::startsWith);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        log.info("token : {}", bearerToken);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
