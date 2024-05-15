package com.orchard.domain.product.domain.pesist;

import com.orchard.domain.cart.domain.persist.Cart;
import com.orchard.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;
    private String name;
    private Integer price;
    private Integer sale;

    private Product(String name, Integer price, Integer sale) {
        this.name = name;
        this.price = price;
        this.sale = sale;
    }

    public static Product of(String name, Integer price, Integer sale) {
        return new Product(name, price, sale);
    }
}
