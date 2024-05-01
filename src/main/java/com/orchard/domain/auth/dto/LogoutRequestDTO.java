package com.orchard.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import com.orchard.global.common.AccessToken;
import com.orchard.global.common.RefreshToken;

@Getter
@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class LogoutRequestDTO {

    @JsonProperty("accessToken")
    private AccessToken accessToken;

    @JsonProperty("refreshToken")
    private RefreshToken refreshToken;
}
