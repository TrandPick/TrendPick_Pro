package project.trendpick_pro.domain.orders.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.delivery.entity.DeliveryState;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

    private Long orderId;
    private String brandName;
    private String orderItemName;
    private LocalDateTime order_date;
    private int totalPrice;
    private String deliveryStatus;

    @Builder
    @QueryProjection
    public OrderResponse(Long orderId, String brandName, String productName, LocalDateTime order_date, int totalPrice, String deliveryStatus) {
        this.orderId = orderId;
        this.brandName = brandName;
        this.orderItemName = productName;
        this.order_date = order_date;
        this.totalPrice = totalPrice;
        this.deliveryStatus = deliveryStatus;
    }
}
