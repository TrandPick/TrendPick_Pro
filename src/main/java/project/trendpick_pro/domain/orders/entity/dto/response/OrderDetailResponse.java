package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.orders.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDetailResponse {
    private Long orderId;
    private String orderStatus;
    private List<OrderResponse> orderItems;
    private int totalPrice = 0;
    private String paymentMethod;
    private LocalDateTime orderDate;

    @Builder
    private OrderDetailResponse(Long orderId, String orderStatus, List<OrderResponse> orderItems, int totalPrice, String paymentMethod, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
    }

    public static OrderDetailResponse of(Order order, List<OrderResponse> orderItems) {
        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus().getValue())
                .orderItems(orderItems)
                .orderDate(order.getCreatedDate())
                .paymentMethod(order.getPaymentMethod())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
