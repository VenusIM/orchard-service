package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.persist.RoleType;
import com.orchard.domain.member.domain.vo.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@ApiModel
public class JoinRequestDTO {

    private String email;
    private String password;
    @JsonProperty("name")
    private String name;
    private String phoneNumber;
    @JsonProperty("postCode")
    private String postCode;
    @JsonProperty("address")
    private String address;

    public Member toEntity() {
        return Member.builder()
                .email(UserEmail.from(email))
                .password(UserPassword.from(password))
                .name(UserName.from(name))
                .roleType(RoleType.USER)
                .phoneNumber(UserPhoneNumber.from(phoneNumber))
                .postCode(UserPostCode.from(postCode))
                .address(UserAddress.from(address))
                .build();
    }
}


