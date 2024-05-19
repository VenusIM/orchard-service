package com.orchard.global.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenDTO {

    @JsonProperty("accessToken")
    private AccessToken accessToken;

    @JsonProperty("refreshToken")
    private RefreshToken refreshToken;

    @JsonProperty("role")
    private String role;

    public TokenDTO(AccessToken accessToken, RefreshToken refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static TokenDTO create(final AccessToken accessToken, final RefreshToken refreshToken) {
        return new TokenDTO(accessToken, refreshToken);
    }

    public void setOriginRefreshToken(RefreshToken refreshToken) {
        this.refreshToken =  refreshToken;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
