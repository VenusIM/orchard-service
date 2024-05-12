package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.orchard.domain.member.domain.vo.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.orchard.domain.member.domain.persist.Member;

import java.time.LocalDate;

@Getter
@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinResponseDTO {

    @JsonProperty("email")
    private UserEmail email;

    @JsonProperty("name")
    private UserName name;

    @JsonProperty("phoneNumber")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UserPhoneNumber phoneNumber;

    @JsonProperty("address")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UserAddress address;

    public static JoinResponseDTO from(final Member member) {
        return new JoinResponseDTO(member.getEmail(), member.getName(),
                member.getPhoneNumber(), member.getAddress());
    }
}
