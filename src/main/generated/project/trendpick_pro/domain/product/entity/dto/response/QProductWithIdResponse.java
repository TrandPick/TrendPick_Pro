package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * project.trendpick_pro.domain.product.entity.dto.response.QProductWithIdResponse is a Querydsl Projection type for ProductWithIdResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QProductWithIdResponse extends ConstructorExpression<ProductWithIdResponse> {

    private static final long serialVersionUID = 173412748L;

    public QProductWithIdResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> brand, com.querydsl.core.types.Expression<? extends project.trendpick_pro.domain.common.file.CommonFile> mainFile, com.querydsl.core.types.Expression<Integer> price) {
        super(ProductWithIdResponse.class, new Class<?>[]{long.class, String.class, String.class, project.trendpick_pro.domain.common.file.CommonFile.class, int.class}, id, name, brand, mainFile, price);
    }

}

