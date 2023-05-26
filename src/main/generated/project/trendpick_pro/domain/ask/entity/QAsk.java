package project.trendpick_pro.domain.ask.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAsk is a Querydsl query type for Ask
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAsk extends EntityPathBase<Ask> {

    private static final long serialVersionUID = -658585546L;

    public static final QAsk ask = new QAsk("ask");

    public final ListPath<project.trendpick_pro.domain.answer.entity.Answer, project.trendpick_pro.domain.answer.entity.QAnswer> answerList = this.<project.trendpick_pro.domain.answer.entity.Answer, project.trendpick_pro.domain.answer.entity.QAnswer>createList("answerList", project.trendpick_pro.domain.answer.entity.Answer.class, project.trendpick_pro.domain.answer.entity.QAnswer.class, PathInits.DIRECT2);

    public final StringPath author = createString("author");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath title = createString("title");

    public QAsk(String variable) {
        super(Ask.class, forVariable(variable));
    }

    public QAsk(Path<? extends Ask> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAsk(PathMetadata metadata) {
        super(Ask.class, metadata);
    }

}

