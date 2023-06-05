package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.Getter;

@Getter
public class OrderItemDto {
    private String productName;
    private int count;
    private int price;
}
