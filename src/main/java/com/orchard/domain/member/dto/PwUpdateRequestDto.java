package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PwUpdateRequestDto {
    @JsonProperty("current")
    private String current;
    @JsonProperty("after")
    private String after;
}
