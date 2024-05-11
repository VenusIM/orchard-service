package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.orchard.domain.member.domain.vo.UserAddress;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.vo.UserName;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class FindAllResponse {

    @JsonProperty("name")
    private UserName name;

    @JsonProperty("profile")
    private UserPhoneNumber phoneNumber;

    @JsonProperty("address")
    private UserAddress address;

    public static FindAllResponse of(final Member member) {
        return new FindAllResponse(member.getName(), member.getPhoneNumber(), member.getAddress());
    }
}
