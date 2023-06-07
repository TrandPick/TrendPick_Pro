package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.Getter;
import lombok.Setter;
import project.trendpick_pro.domain.product.entity.Product;

@Getter
@Setter
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int count;
    private int price;

    public OrderItemDto(Product product, int count) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice();
        this.count = count;
    }

    public OrderItemDto() {
    }

    public int getTotalPrice(){
        return getPrice() * count;
    }
}
