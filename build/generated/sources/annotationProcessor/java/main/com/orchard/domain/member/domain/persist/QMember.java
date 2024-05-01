package com.orchard.domain.member.domain.persist;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1182285188L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final com.orchard.global.common.QBaseTimeEntity _super = new com.orchard.global.common.QBaseTimeEntity(this);

    public final com.orchard.domain.member.domain.vo.QUserAddress address;

    public final DatePath<java.time.LocalDate> birth = createDate("birth", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createTime = _super.createTime;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedTime = _super.deletedTime;

    public final com.orchard.domain.member.domain.vo.QUserEmail email;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.orchard.domain.member.domain.vo.QUserName name;

    public final com.orchard.domain.member.domain.vo.QUserNickName nickname;

    public final com.orchard.domain.member.domain.vo.QUserPassword password;

    public final com.orchard.domain.member.domain.vo.QUserPhoneNumber phoneNumber;

    public final EnumPath<RoleType> roleType = createEnum("roleType", RoleType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.orchard.domain.member.domain.vo.QUserAddress(forProperty("address")) : null;
        this.email = inits.isInitialized("email") ? new com.orchard.domain.member.domain.vo.QUserEmail(forProperty("email")) : null;
        this.name = inits.isInitialized("name") ? new com.orchard.domain.member.domain.vo.QUserName(forProperty("name")) : null;
        this.nickname = inits.isInitialized("nickname") ? new com.orchard.domain.member.domain.vo.QUserNickName(forProperty("nickname")) : null;
        this.password = inits.isInitialized("password") ? new com.orchard.domain.member.domain.vo.QUserPassword(forProperty("password")) : null;
        this.phoneNumber = inits.isInitialized("phoneNumber") ? new com.orchard.domain.member.domain.vo.QUserPhoneNumber(forProperty("phoneNumber")) : null;
    }

}

