package project.trendpick_pro.domain.orders.entity.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.trendpick_pro.domain.orders.entity.Order;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long orderId;
    private List<OrderResponse> orderItems;
    private int totalPrice = 0;
    private String paymentMethod;
    private LocalDateTime orderDate;

    public static OrderDetailResponse of(Order order, List<OrderResponse> orderItems){
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
        orderDetailResponse.orderId = order.getId();
        orderDetailResponse.orderItems = orderItems;
        orderDetailResponse.orderDate = order.getCreatedDate();
        orderDetailResponse.paymentMethod = order.getPaymentMethod();
        for (OrderResponse orderItem : orderItems) {
            orderDetailResponse.totalPrice += orderItem.getTotalPrice();
        }
        return orderDetailResponse;
    }

    public String getFormattedPaymentPrice(){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(getTotalPrice());
    }

}
