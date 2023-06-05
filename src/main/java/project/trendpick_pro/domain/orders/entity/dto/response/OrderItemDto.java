package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private String productName;
    private int count;
    private int price;

    public OrderItemDto(String productName, int count, int price) {
        this.productName = productName;
        this.count = count;
        this.price = price;
    }

    public OrderItemDto() {
    }
}
