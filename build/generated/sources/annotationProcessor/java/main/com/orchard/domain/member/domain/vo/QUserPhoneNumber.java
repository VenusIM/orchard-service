package com.orchard.domain.member.domain.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPhoneNumber is a Querydsl query type for UserPhoneNumber
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserPhoneNumber extends BeanPath<UserPhoneNumber> {

    private static final long serialVersionUID = -377002585L;

    public static final QUserPhoneNumber userPhoneNumber = new QUserPhoneNumber("userPhoneNumber");

    public final StringPath phoneNumber = createString("phoneNumber");

    public QUserPhoneNumber(String variable) {
        super(UserPhoneNumber.class, forVariable(variable));
    }

    public QUserPhoneNumber(Path<UserPhoneNumber> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPhoneNumber(PathMetadata metadata) {
        super(UserPhoneNumber.class, metadata);
    }

}

