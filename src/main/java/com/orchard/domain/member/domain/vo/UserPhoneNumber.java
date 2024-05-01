package com.orchard.domain.member.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserPhoneNumber {

    @NotBlank(message = "번호는 필수 입력입니다.")
    @Column(name = "phone_number", nullable = false, length = 13)
    private String phoneNumber;

    public static UserPhoneNumber from(final String phoneNumber) {
        return new UserPhoneNumber(phoneNumber);
    }

    @JsonValue
    public String userPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPhoneNumber profile = (UserPhoneNumber) o;
        return Objects.equals(userPhoneNumber(), profile.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPhoneNumber());
    }
}
