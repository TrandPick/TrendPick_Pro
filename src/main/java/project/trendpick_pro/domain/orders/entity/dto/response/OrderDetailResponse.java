package project.trendpick_pro.domain.orders.entity.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.trendpick_pro.domain.orders.entity.Order;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private List<OrderResponse> orderItems;
    private int totalPrice = 0;
    private String paymentMethod;

    public static OrderDetailResponse of(Order order, List<OrderResponse> orderItems){
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
        orderDetailResponse.orderItems = orderItems;
        orderDetailResponse.paymentMethod = order.getPaymentMethod();
        for (OrderResponse orderItem : orderItems) {
            orderDetailResponse.totalPrice += orderItem.getTotalPrice();
        }
        return orderDetailResponse;
    }
}
