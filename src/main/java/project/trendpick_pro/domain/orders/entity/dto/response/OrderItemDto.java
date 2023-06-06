package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private Long productId;
    private String productName;
    private String size;
    private int count;
    private int price;

    public OrderItemDto(Long productId, String productName, String size, int count, int price) {
        this.productName = productName;
        this.count = count;
        this.price = price;
        this.productId = productId;
        this.size = size;
    }

    public OrderItemDto() {
    }

    public int getTotalPrice(){
        return getPrice() * count;
    }
}
