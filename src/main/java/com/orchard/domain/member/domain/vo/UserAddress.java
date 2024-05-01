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
public final class UserAddress {

    @NotBlank(message = "주소는 필수 입력사항 입니다.")
    @Column(unique = true, nullable = false, length = 200)
    private String address;

    public static UserAddress from(final String address) {
        return new UserAddress(address);
    }

    @JsonValue
    public String userAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAddress useraddress = (UserAddress) o;
        return Objects.equals(userAddress(), useraddress.userAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAddress());
    }
}
