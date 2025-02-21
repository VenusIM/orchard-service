package com.orchard.domain.auth.details;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.persist.MemberRepository;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.error.exception.MemberNotFoundException;
import com.orchard.global.error.exception.ErrorCode;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomMemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(UserEmail.from(email))
                .map(this::createUserDetails)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private UserDetails createUserDetails(final Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + member.getRoleType());

        return new User(member.getEmail().userEmail(),
                member.getPassword().userPassword(), Collections.singleton(grantedAuthority));
    }
}
