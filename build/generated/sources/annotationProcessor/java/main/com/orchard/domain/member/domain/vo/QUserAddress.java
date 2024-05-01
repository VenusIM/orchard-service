package com.orchard.domain.member.domain.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserAddress is a Querydsl query type for UserAddress
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserAddress extends BeanPath<UserAddress> {

    private static final long serialVersionUID = 410283844L;

    public static final QUserAddress userAddress = new QUserAddress("userAddress");

    public final StringPath address = createString("address");

    public QUserAddress(String variable) {
        super(UserAddress.class, forVariable(variable));
    }

    public QUserAddress(Path<UserAddress> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAddress(PathMetadata metadata) {
        super(UserAddress.class, metadata);
    }

}

