package project.trendpick_pro.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1765768292L;

    public static final QMember member = new QMember("member1");

    public final NumberPath<Long> account = createNumber("account", Long.class);

    public final StringPath bank_name = createString("bank_name");

    public final DateTimePath<java.util.Date> birth = createDateTime("birth", java.util.Date.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final StringPath phone_num = createString("phone_num");

    public final EnumPath<RoleType> role = createEnum("role", RoleType.class);

    public final NumberPath<Long> tag_id = createNumber("tag_id", Long.class);

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

