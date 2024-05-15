package com.orchard.domain.member.domain.persist;

import com.orchard.domain.cart.domain.persist.Cart;
import com.orchard.domain.order.domain.persist.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.orchard.domain.member.domain.vo.*;
import com.orchard.global.common.BaseTimeEntity;

import jakarta.persistence.*;

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
    private UserPostCode postCode;

    @Embedded
    private UserAddress address;

    @Embedded
    private UserPhoneNumber phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Builder
    public Member(UserEmail email, UserPassword password, UserName name, RoleType roleType,
                  UserPhoneNumber phoneNumber, UserPostCode postCode, UserAddress address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.roleType = roleType;
        this.phoneNumber = phoneNumber;
        this.postCode = postCode;
        this.address = address;
    }

    /**
     * 비즈 니스 로직
     */
    public Member update(final Member member, final PasswordEncoder encoder) {
        changeEmail(member.email);
        changePassword(member.password);
        changePhoneNumber(member.phoneNumber);
        changePostCode(member.postCode);
        changeAddress(member.address);
        encode(encoder);
        return this;
    }

    private void changePostCode(UserPostCode postCode) {
        this.postCode = postCode;
    }

    private void changeEmail(UserEmail email) {
        this.email = email;
    }

    private void changePassword(UserPassword password) {
        this.password = password;
    }

    private void changePhoneNumber(UserPhoneNumber phoneNumber) { this.phoneNumber = phoneNumber; }

    private void changeAddress(UserAddress address) { this.address = address; }

    // 비밀번호 해시화
    public Member encode(final PasswordEncoder encoder) {
        password = UserPassword.encode(password.userPassword(), encoder);
        return this;
    }
}
