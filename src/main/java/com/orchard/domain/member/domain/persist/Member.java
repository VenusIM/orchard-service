package com.orchard.domain.member.domain.persist;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.orchard.domain.member.domain.vo.*;
import com.orchard.global.common.BaseTimeEntity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Embedded
    private UserEmail email;

    @Embedded
    private UserPassword password;

    @Embedded
    private UserName name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 20)
    private RoleType roleType;

    @Embedded
    private UserNickName nickname;

    @DateTimeFormat
    private LocalDate birth;

    @Embedded
    private UserAddress address;

    @Embedded
    private UserPhoneNumber phoneNumber;

    @Builder
    public Member(UserEmail email, UserPassword password, UserName name, RoleType roleType,
                  UserNickName nickname, LocalDate birth, UserPhoneNumber phoneNumber, UserAddress address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.roleType = roleType;
        this.nickname = nickname;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    /**
     * 비즈 니스 로직
     */
    public Member update(final Member member, final PasswordEncoder encoder) {
        changeEmail(member.email);
        changePassword(member.password);
        changeNickName(member.nickname);
        changePhoneNumber(member.phoneNumber);
        encode(encoder);
        return this;
    }

    private void changeEmail(UserEmail email) {
        this.email = email;
    }

    private void changePassword(UserPassword password) {
        this.password = password;
    }

    private void changeNickName(UserNickName nickname) {
        this.nickname = nickname;
    }

    private void changePhoneNumber(UserPhoneNumber phoneNumber) { this.phoneNumber = phoneNumber; }

    private void changeAddress(UserAddress address) { this.address = address; }

    // 비밀번호 해시화
    public Member encode(final PasswordEncoder encoder) {
        password = UserPassword.encode(password.userPassword(), encoder);
        return this;
    }
}
