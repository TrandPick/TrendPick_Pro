package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.*;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.product.entity.Product;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int count;
    private int price;

    public static OrderItemDto of(Product product, int count) {
        return OrderItemDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .count(count)
                .build();
    }
    @Builder
    public OrderItemDto(Long productId, String productName, int price, int count) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.count = count;
    }

    public int getTotalPrice(){
        return getPrice() * count;
    }
}
