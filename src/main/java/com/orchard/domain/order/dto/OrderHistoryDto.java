package com.orchard.domain.order.dto;

import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.vo.UserAddress;
import com.orchard.domain.member.domain.vo.UserName;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import com.orchard.domain.order.domain.persist.Order;
import com.orchard.domain.product.domain.pesist.Product;

public class OrderHistoryDto {
    private Long id;
    private String tid;
    private String orderNo;
    private Member member;
    private Integer count;
    private UserName userName;
    private UserPhoneNumber userPhoneNumber;
    private UserAddress userAddress;
    private String status;
    private Product product;

    private String receiptUrl;

    private String signature;

    private String transNo;

    public OrderHistoryDto(Order order) {
        this.id = order.getId();
        this.tid = order.getTid();
        this.orderNo = order.getOrderNo();
        this.count = order.getCount();
        this.userName = order.getUserName();
        this.userPhoneNumber = order.getUserPhoneNumber();
        this.userAddress = order.getUserAddress();
        this.status = status;
        this.product = product;
        this.receiptUrl = receiptUrl;
        this.signature = signature;
        this.transNo = transNo;
    }
}
