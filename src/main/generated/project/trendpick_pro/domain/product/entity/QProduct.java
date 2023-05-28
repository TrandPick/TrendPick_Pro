package project.trendpick_pro.domain.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -674347658L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final project.trendpick_pro.domain.common.base.QBaseTimeEntity _super = new project.trendpick_pro.domain.common.base.QBaseTimeEntity(this);

    public final StringPath brand = createString("brand");

    public final project.trendpick_pro.domain.common.file.QCommonFile commonFile;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mainCategory = createString("mainCategory");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final NumberPath<Integer> stock = createNumber("stock", Integer.class);

    public final ListPath<String, StringPath> subCategory = this.<String, StringPath>createList("subCategory", String.class, StringPath.class, PathInits.DIRECT2);

    public final ListPath<project.trendpick_pro.domain.tag.entity.Tag, project.trendpick_pro.domain.tag.entity.QTag> tags = this.<project.trendpick_pro.domain.tag.entity.Tag, project.trendpick_pro.domain.tag.entity.QTag>createList("tags", project.trendpick_pro.domain.tag.entity.Tag.class, project.trendpick_pro.domain.tag.entity.QTag.class, PathInits.DIRECT2);

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.commonFile = inits.isInitialized("commonFile") ? new project.trendpick_pro.domain.common.file.QCommonFile(forProperty("commonFile"), inits.get("commonFile")) : null;
    }

}

