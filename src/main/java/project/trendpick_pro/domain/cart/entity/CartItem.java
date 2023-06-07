package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.product.entity.Product;

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

    private int count; // 해당 상품 수량

    @Builder
    public CartItem(Cart cart, Product product, int count) {
        this.cart = cart;
        this.product = product;
        this.count = count;
    }
    public static CartItem of(Cart cart, Product product, CartItemRequest cartItemRequest){
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .count(cartItemRequest.getCount())
                .build();
    }
    public void addCount(int count){
        this.count += count;
    }

    public void update(int count){
        this.count=count;
    }
}