package com.orchard.domain.member.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.orchard.domain.member.error.exception.PasswordMissMatchException;
import com.orchard.domain.member.error.exception.PasswordNullException;
import com.orchard.global.error.exception.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserPassword {

    @NotBlank(message = "비밀번호는 필수 입력사항 입니다.")
    @Column(nullable = false, length = 100)
    private String password;

    public static UserPassword encode(final String rawPassword, final PasswordEncoder passwordEncoder) {
        validateBlank(rawPassword);
        return new UserPassword(passwordEncoder.encode(rawPassword));
    }

    public static UserPassword from(final String password) {
        return new UserPassword(password);
    }

    private static void validateBlank(String rawPassword) {
        if (Objects.isNull(rawPassword) || rawPassword.isBlank()) {
            throw new PasswordNullException(ErrorCode.PASSWORD_NULL_ERROR);
        }
    }

    public void matches(final UserPassword other, final PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(other.password, this.password)) {
            throw new PasswordMissMatchException(ErrorCode.PASSWORD_MISS_MATCH);
        }
    }

    @JsonValue
    public String userPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPassword userPassword = (UserPassword) o;
        return Objects.equals(userPassword(), userPassword.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPassword());
    }
}
