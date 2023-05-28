package project.trendpick_pro.domain.ask.entity.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * project.trendpick_pro.domain.ask.entity.dto.response.QAskResponse is a Querydsl Projection type for AskResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QAskResponse extends ConstructorExpression<AskResponse> {

    private static final long serialVersionUID = 1378042495L;

    public QAskResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> author, com.querydsl.core.types.Expression<String> brand, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<String> content, com.querydsl.core.types.Expression<? extends java.util.List<project.trendpick_pro.domain.answer.entity.Answer>> answerList) {
        super(AskResponse.class, new Class<?>[]{long.class, String.class, String.class, String.class, String.class, java.util.List.class}, id, author, brand, title, content, answerList);
    }

}

