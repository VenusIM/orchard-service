package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.persist.RoleType;
import com.orchard.domain.member.domain.vo.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@ApiModel
@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class JoinRequestDTO {

    @Valid
    @JsonProperty("email")
    @ApiModelProperty(example = "yim3370@gmail.com")
    private UserEmail userEmail;

    @Valid
    @JsonProperty("password")
    @ApiModelProperty(example = "1234")
    private UserPassword userPassword;

    @Valid
    @JsonProperty("name")
    @ApiModelProperty(example = "임준희")
    private UserName name;

    @JsonProperty("birth")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd")
    @ApiModelProperty(example = "1999-10-10")
    private LocalDate birth;

    @JsonProperty("phoneNumber")
    @ApiModelProperty(example = "010-1234-5678")
    private UserPhoneNumber phoneNumber;

    @JsonProperty("address")
    @ApiModelProperty(example = "서울시 강남구 대치동")
    private UserAddress address;


    private JoinRequestDTO(UserEmail userEmail, UserPassword userPassword, UserName name,
                           LocalDate birth, UserPhoneNumber phoneNumber, UserAddress address) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.name = name;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public static JoinRequestDTO from(final Member member) {
        return new JoinRequestDTO(member.getEmail(), member.getPassword(), member.getName(), member.getBirth(),
                member.getPhoneNumber(), member.getAddress());
    }

    public Member toEntity() {
        return Member.builder()
                .email(userEmail)
                .password(userPassword)
                .name(name)
                .roleType(RoleType.USER)
                .birth(birth)
                .phoneNumber(phoneNumber)
                .address(address)
                .build();
    }
}


