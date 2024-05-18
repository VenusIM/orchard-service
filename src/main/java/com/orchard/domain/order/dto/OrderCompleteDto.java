package com.orchard.domain.order.dto;

import com.orchard.domain.member.domain.vo.UserAddress;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderCompleteDto {
    private List<OrderResponseDto> orders;
    private UserAddress address;
    private UserPhoneNumber phoneNumber;
    private String receiptUrl;
    private String orderId;
}
