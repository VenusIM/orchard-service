package com.orchard.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Long productId;

    private String price;

    private int count;
}
