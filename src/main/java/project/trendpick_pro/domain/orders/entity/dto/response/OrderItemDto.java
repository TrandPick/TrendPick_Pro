package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.*;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.text.NumberFormat;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private String size;
    private String color;
    private int price;
    private Long cartItemId;

    public static OrderItemDto of(Product product, int quantity, String size, String color) {
        return OrderItemDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getProductOption().getPrice())
                .quantity(quantity)
                .size(size)
                .color(color)
                .cartItemId(0L)
                .build();
    }

    public static OrderItemDto of(Product product, int quantity, String size, String color,Long cartItemId) {
        return OrderItemDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getProductOption().getPrice())
                .quantity(quantity)
                .size(size)
                .color(color)
                .cartItemId(cartItemId)
                .build();
    }

    public int getTotalPrice(){
        return this.price * this.quantity;
    }

    public String getFormattedTotalPrice() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(getTotalPrice()) + "원";
    }
}
