package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.vo.*;

import java.time.LocalDate;

@Getter
@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponseDTO {

    @JsonProperty("email")
    private UserEmail email;

    @JsonProperty("name")
    private UserName name;

    @JsonProperty("birth")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @JsonProperty("phoneNumber")
    private UserPhoneNumber phoneNumber;

    @JsonProperty("address")
    private UserAddress address;

    /**
     *  생성 로직
     */
    public static MemberResponseDTO create(final Member member) {
        return new MemberResponseDTO(member.getEmail(), member.getName(), member.getBirth(), member.getPhoneNumber(), member.getAddress());
    }
}
