package com.orchard.domain.order.domain.persist;

import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.vo.UserAddress;
import com.orchard.domain.member.domain.vo.UserName;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import com.orchard.domain.product.domain.pesist.Product;
import com.orchard.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders",indexes = {
        @Index(name = "fk_order_product_id", columnList = "product_id"),
        @Index(name = "fk_order_order_no", columnList = "order_no"),
        @Index(name = "fk_order_member_idx", columnList = "member_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private String tid;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "member_id")
    private Long memberIdx;

    private Integer count;

    private UserName userName;

    private UserPhoneNumber userPhoneNumber;

    private UserAddress userAddress;

    private String status;

    @Column(name = "product_id")
    private Long productIdx;

    private String receiptUrl;

    private String signature;

    @Builder
    public Order(String orderNo, Long memberIdx, UserName userName, UserPhoneNumber userPhoneNumber, UserAddress userAddress, String status, Long productIdx, Integer count) {
        this.orderNo = orderNo;
        this.memberIdx = memberIdx;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userAddress = userAddress;
        this.status = status;
        this.productIdx = productIdx;
        this.count = count;
    }

    public void updateOrder(String tid, String status, String receiptUrl, String signature) {
        this.tid = tid;
        this.status = status;
        this.receiptUrl = receiptUrl;
        this.signature = signature;
    }
}
