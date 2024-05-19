package com.orchard.domain.order.dto;

import com.orchard.domain.member.domain.vo.UserAddress;
import com.orchard.domain.member.domain.vo.UserName;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import com.orchard.domain.order.domain.persist.Order;
import com.orchard.domain.product.domain.pesist.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDto {
    private String orderNo;
    private Long memberIdx;
    private UserName userName;
    private UserPhoneNumber userPhoneNumber;
    private UserAddress userAddress;
    private String status;
    private Product product;
    private Integer count;
    private String clientId;
    private String host;

    public OrderResponseDto(Order order) {
        this.orderNo = order.getOrderNo();
        this.memberIdx = order.getMemberIdx();
        this.userName = order.getUserName();
        this.userAddress = order.getUserAddress();
        this.userPhoneNumber = order.getUserPhoneNumber();
        this.status = order.getStatus();
        this.count = order.getCount();
    }
}
