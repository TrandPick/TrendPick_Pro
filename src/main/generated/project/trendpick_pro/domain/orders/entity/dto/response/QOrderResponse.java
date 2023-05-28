package project.trendpick_pro.domain.orders.entity.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * project.trendpick_pro.domain.orders.entity.dto.response.QOrderResponse is a Querydsl Projection type for OrderResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderResponse extends ConstructorExpression<OrderResponse> {

    private static final long serialVersionUID = 695514566L;

    public QOrderResponse(com.querydsl.core.types.Expression<Long> orderId, com.querydsl.core.types.Expression<String> brandName, com.querydsl.core.types.Expression<String> productName, com.querydsl.core.types.Expression<java.time.LocalDateTime> order_date, com.querydsl.core.types.Expression<Integer> totalPrice, com.querydsl.core.types.Expression<String> deliveryStatus) {
        super(OrderResponse.class, new Class<?>[]{long.class, String.class, String.class, java.time.LocalDateTime.class, int.class, String.class}, orderId, brandName, productName, order_date, totalPrice, deliveryStatus);
    }

}

