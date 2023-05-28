package project.trendpick_pro.domain.tag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTag is a Querydsl query type for Tag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTag extends EntityPathBase<Tag> {

    private static final long serialVersionUID = -1204291178L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTag tag = new QTag("tag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final project.trendpick_pro.domain.product.entity.QProduct product;

    public QTag(String variable) {
        this(Tag.class, forVariable(variable), INITS);
    }

    public QTag(Path<? extends Tag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTag(PathMetadata metadata, PathInits inits) {
        this(Tag.class, metadata, inits);
    }

    public QTag(Class<? extends Tag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new project.trendpick_pro.domain.product.entity.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

