package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * project.trendpick_pro.domain.product.entity.dto.response.QProductListResponse is a Querydsl Projection type for ProductListResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QProductListResponse extends ConstructorExpression<ProductListResponse> {

    private static final long serialVersionUID = -1483401431L;

    public QProductListResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> brand, com.querydsl.core.types.Expression<? extends project.trendpick_pro.domain.common.file.CommonFile> mainFile, com.querydsl.core.types.Expression<Integer> price) {
        super(ProductListResponse.class, new Class<?>[]{long.class, String.class, String.class, project.trendpick_pro.domain.common.file.CommonFile.class, int.class}, id, name, brand, mainFile, price);
    }

}

