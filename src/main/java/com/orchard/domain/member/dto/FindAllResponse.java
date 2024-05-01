package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.vo.UserName;
import com.orchard.domain.member.domain.vo.UserNickName;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class FindAllResponse {

    @JsonProperty("name")
    private UserName name;

    @JsonProperty("nickname")
    private UserNickName nickname;

    @JsonProperty("profile")
    private UserPhoneNumber image;

    public static FindAllResponse of(final Member member) {
        return new FindAllResponse(member.getName(), member.getNickname(), null);
    }
}
