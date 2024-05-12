package com.orchard.domain.member.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public final class UserPostCode {

    @NotBlank(message = "우편 번호는 필수 입력사항 입니다.")
    @Column(unique = true, nullable = false, length = 200)
    private String postCode;

    public static UserPostCode from(final String postCode) {
        return new UserPostCode(postCode);
    }

    @JsonValue
    public String userPostCode() {
        return postCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPostCode userpostCode = (UserPostCode) o;
        return Objects.equals(userPostCode(), userpostCode.userPostCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPostCode());
    }
}
