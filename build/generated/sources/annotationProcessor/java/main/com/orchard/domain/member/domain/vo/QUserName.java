package com.orchard.domain.member.domain.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserName is a Querydsl query type for UserName
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserName extends BeanPath<UserName> {

    private static final long serialVersionUID = -1054348997L;

    public static final QUserName userName = new QUserName("userName");

    public final StringPath name = createString("name");

    public QUserName(String variable) {
        super(UserName.class, forVariable(variable));
    }

    public QUserName(Path<UserName> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserName(PathMetadata metadata) {
        super(UserName.class, metadata);
    }

}

