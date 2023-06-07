package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.Getter;
import lombok.Setter;
import project.trendpick_pro.domain.product.entity.Product;

@Getter
@Setter
public class OrderItemDto {
    private Long productId;
    private String productName;
    private String size;
    private int count;
    private int price;

    public OrderItemDto(Product product, String size, int count) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice();
        this.count = count;
        this.size = size;
    }

    public OrderItemDto() {
    }

    public int getTotalPrice(){
        return getPrice() * count;
    }
}
