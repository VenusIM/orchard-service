package com.orchard.domain.auth.domain.persist;

import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.global.common.RefreshToken;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private RefreshToken refreshToken;

    @Embedded
    private UserEmail email;

    public RefreshTokenEntity(RefreshToken token, UserEmail email) {
        this.refreshToken = token;
        this.email = email;
    }

    public RefreshTokenEntity updateToken(RefreshToken token) {
        this.refreshToken = token;
        return this;
    }
}
