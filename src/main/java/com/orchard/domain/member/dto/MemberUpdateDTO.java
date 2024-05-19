package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.orchard.domain.member.domain.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.orchard.domain.member.domain.persist.Member;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@ApiModel
@JsonTypeName("user")
@Getter
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class MemberUpdateDTO {

    @JsonProperty("email")
    @ApiModelProperty(example = "yim3370@gmail.com")
    private UserEmail email;

    @JsonProperty("password")
    @ApiModelProperty(example = "3245")
    private UserPassword password;

    @JsonProperty("phoneNumber")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UserPhoneNumber phoneNumber;

    @JsonProperty("address")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UserAddress address;

    private MemberUpdateDTO(UserEmail email, UserPassword password, UserPhoneNumber phoneNumber, UserAddress address) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public static MemberUpdateDTO from(final Member member) {
        return new MemberUpdateDTO(member.getEmail(), member.getPassword(), member.getPhoneNumber(), member.getAddress());
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .address(address)
                .build();
    }
}
