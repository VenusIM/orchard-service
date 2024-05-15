package com.orchard.domain.cart.domain.persist;

import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.product.domain.pesist.Product;
import com.orchard.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "cart_id")
    private Long id;
    private Integer count;

    @OneToOne
    @JoinColumn(name= "product_id")
    private Product product;


    private Cart(Integer count) {
        this.count = count;
    }

    public static Cart from(Integer count) {
        return new Cart(count);
    }
}