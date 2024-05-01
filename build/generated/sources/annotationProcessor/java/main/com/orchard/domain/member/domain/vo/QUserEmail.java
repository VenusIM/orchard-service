package com.orchard.domain.member.domain.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserEmail is a Querydsl query type for UserEmail
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserEmail extends BeanPath<UserEmail> {

    private static final long serialVersionUID = 1666953964L;

    public static final QUserEmail userEmail = new QUserEmail("userEmail");

    public final StringPath email = createString("email");

    public QUserEmail(String variable) {
        super(UserEmail.class, forVariable(variable));
    }

    public QUserEmail(Path<UserEmail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEmail(PathMetadata metadata) {
        super(UserEmail.class, metadata);
    }

}

