package com.orchard.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.domain.vo.UserPassword;


@Getter
@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LoginRequestDto {

    @JsonProperty("email")
    private UserEmail email;

    @JsonProperty("password")
    private UserPassword password;
}
