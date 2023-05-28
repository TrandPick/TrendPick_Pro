package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * project.trendpick_pro.domain.product.entity.dto.response.QProductResponse is a Querydsl Projection type for ProductResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QProductResponse extends ConstructorExpression<ProductResponse> {

    private static final long serialVersionUID = -274979477L;

    public QProductResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> mainCategory, com.querydsl.core.types.Expression<? extends java.util.List<String>> subCategory, com.querydsl.core.types.Expression<String> brand, com.querydsl.core.types.Expression<String> description, com.querydsl.core.types.Expression<String> mainFile, com.querydsl.core.types.Expression<? extends java.util.List<String>> subFiles, com.querydsl.core.types.Expression<Integer> price, com.querydsl.core.types.Expression<Integer> stock, com.querydsl.core.types.Expression<? extends java.util.List<project.trendpick_pro.domain.tag.entity.Tag>> tags) {
        super(ProductResponse.class, new Class<?>[]{long.class, String.class, String.class, java.util.List.class, String.class, String.class, String.class, java.util.List.class, int.class, int.class, java.util.List.class}, id, name, mainCategory, subCategory, brand, description, mainFile, subFiles, price, stock, tags);
    }

}

