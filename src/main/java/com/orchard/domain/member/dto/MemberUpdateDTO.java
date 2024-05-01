package com.orchard.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.domain.vo.UserNickName;
import com.orchard.domain.member.domain.vo.UserPassword;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@ApiModel
@JsonTypeName("user")
@Getter
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class MemberUpdateDTO {

    @JsonProperty("email")
    @ApiModelProperty(example = "golf@gmail.com")
    private UserEmail email;

    @JsonProperty("password")
    @ApiModelProperty(example = "3245")
    private UserPassword password;

    @JsonProperty("nickname")
    @ApiModelProperty(example = "golf")
    private UserNickName nickName;

    @JsonProperty("profile")
    @ApiModelProperty(example = "/user/image/new_image.jpeg")
    private UserPhoneNumber profileImage;

    private MemberUpdateDTO(UserEmail email, UserPassword password, UserNickName nickName,
                           UserPhoneNumber profileImage) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.profileImage = profileImage;
    }

    public static MemberUpdateDTO from(final Member member) {
        return new MemberUpdateDTO(member.getEmail(), member.getPassword(), member.getNickname(),
                null);
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickName)

                .build();
    }
}
