package com.orchard.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orchard.domain.member.domain.vo.UserAddress;
import com.orchard.domain.member.domain.vo.UserName;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Getter
@ToString
public class OrderRequestDto {

    @JsonProperty("phoneNumber")
    private UserPhoneNumber phoneNumber;

    @JsonProperty("userName")
    private UserName userName;

    @JsonProperty("address")
    private UserAddress userAddress;

    private Long productId;

    private String price;

    private int count;
}
