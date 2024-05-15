package com.orchard.domain.auth.domain.persist;

import lombok.*;
import com.orchard.domain.member.domain.vo.*;
import com.orchard.global.common.BaseTimeEntity;

import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @Embedded
    private UserPhoneNumber phoneNumber;
    private Integer count;
    private String code;

    private Message(UserPhoneNumber userPhoneNumber, Integer count, String code) {
        this.phoneNumber = userPhoneNumber;
        this.count = count;
        this.code = code;
    }

    public static Message of(UserPhoneNumber userPhoneNumber, Integer count, String code) {
        return new Message(userPhoneNumber, count, code);
    }
    public void updateCode(String code) {
        this.code = code;
    }
    public void increaseCount() { this.count = count + 1; }


}
