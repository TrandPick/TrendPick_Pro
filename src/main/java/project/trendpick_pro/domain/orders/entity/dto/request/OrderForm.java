package project.trendpick_pro.domain.orders.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import project.trendpick_pro.domain.orders.entity.OrderItem;

import java.util.List;

//주문서
public class OrderForm {

    private String username;
    private String phoneNum;
    private String address;
    private String message;
    private List<OrderItem> orderItemList;
    private String paymentMethod;

}
