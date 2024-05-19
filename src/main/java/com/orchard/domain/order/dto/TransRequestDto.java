package com.orchard.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TransRequestDto {

    @JsonProperty("orderNo")
    private String orderNo;

    @JsonProperty("transNo")
    private String transNo;

    @JsonProperty("transCompany")
    private String transCompany;
}