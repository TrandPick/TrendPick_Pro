package project.trendpick_pro.domain.answer.entity.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * project.trendpick_pro.domain.answer.entity.dto.response.QAnswerResponse is a Querydsl Projection type for AnswerResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QAnswerResponse extends ConstructorExpression<AnswerResponse> {

    private static final long serialVersionUID = -2046614161L;

    public QAnswerResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> author, com.querydsl.core.types.Expression<? extends project.trendpick_pro.domain.ask.entity.Ask> ask, com.querydsl.core.types.Expression<String> content, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdDate, com.querydsl.core.types.Expression<java.time.LocalDateTime> modifiedDate) {
        super(AnswerResponse.class, new Class<?>[]{long.class, String.class, project.trendpick_pro.domain.ask.entity.Ask.class, String.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class}, id, author, ask, content, createdDate, modifiedDate);
    }

}

