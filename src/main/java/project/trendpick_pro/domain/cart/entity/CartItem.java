package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.ProductOption;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private String color; // 해당 상품 색상

    private String size; // 해당 상품 사이즈
    private int count; // 해당 상품 수량

    @Builder
    public CartItem(Cart cart, Product product, String color, String size, int count) {
        this.cart = cart;
        this.product = product;
        this.color = color;
        this.size = size;
        this.count = count;
    }
    public static CartItem of(Cart cart, Product product, CartItemRequest cartItemRequest){
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .color(cartItemRequest.getColor())
                .size(cartItemRequest.getSize())
                .count(cartItemRequest.getCount())
                .build();
    }
    public void addCount(int count){
        this.count += count;
    }
}