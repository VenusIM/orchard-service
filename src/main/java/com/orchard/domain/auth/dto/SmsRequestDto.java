package com.orchard.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orchard.domain.auth.domain.persist.Message;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SmsRequestDto {
    @JsonProperty("number")
    private UserPhoneNumber phoneNumber;

    @JsonProperty("code")
    private String code;
}
