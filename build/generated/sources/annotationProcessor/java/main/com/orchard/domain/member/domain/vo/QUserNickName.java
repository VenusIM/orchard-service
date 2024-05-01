package com.orchard.domain.member.domain.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserNickName is a Querydsl query type for UserNickName
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserNickName extends BeanPath<UserNickName> {

    private static final long serialVersionUID = 1122349886L;

    public static final QUserNickName userNickName = new QUserNickName("userNickName");

    public final StringPath nickname = createString("nickname");

    public QUserNickName(String variable) {
        super(UserNickName.class, forVariable(variable));
    }

    public QUserNickName(Path<UserNickName> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserNickName(PathMetadata metadata) {
        super(UserNickName.class, metadata);
    }

}

