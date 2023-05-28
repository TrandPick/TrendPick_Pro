package project.trendpick_pro.domain.common.file;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommonFile is a Querydsl query type for CommonFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommonFile extends EntityPathBase<CommonFile> {

    private static final long serialVersionUID = 30214313L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommonFile commonFile = new QCommonFile("commonFile");

    public final ListPath<CommonFile, QCommonFile> child = this.<CommonFile, QCommonFile>createList("child", CommonFile.class, QCommonFile.class, PathInits.DIRECT2);

    public final StringPath FileName = createString("FileName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QCommonFile parent;

    public QCommonFile(String variable) {
        this(CommonFile.class, forVariable(variable), INITS);
    }

    public QCommonFile(Path<? extends CommonFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommonFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommonFile(PathMetadata metadata, PathInits inits) {
        this(CommonFile.class, metadata, inits);
    }

    public QCommonFile(Class<? extends CommonFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parent = inits.isInitialized("parent") ? new QCommonFile(forProperty("parent"), inits.get("parent")) : null;
    }

}

