package project.trendpick_pro.domain.orders.entity.dto.response;

import lombok.Builder;
import lombok.Getter;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.text.NumberFormat;
import java.util.Locale;

@Getter
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private String size;
    private String color;
    private int price;
    private Long cartItemId;

    @Builder
    private OrderItemDto(Long productId, String productName, int quantity, String size, String color, int price, Long cartItemId) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.price = price;
        this.cartItemId = cartItemId;
    }

    public static OrderItemDto of(Product product, int quantity, String size, String color) {
        return OrderItemDto.builder()
                .productId(product.getId())
                .productName(product.getTitle())
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
                .productName(product.getTitle())
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
