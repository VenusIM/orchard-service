package com.orchard.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import lombok.Getter;
import lombok.ToString;
@Getter
@ToString
public class OrderSearchRequestDto {

    @JsonProperty("phoneNumber")
    private UserPhoneNumber phoneNumber;

    @JsonProperty("orderId")
    private String orderId;
}