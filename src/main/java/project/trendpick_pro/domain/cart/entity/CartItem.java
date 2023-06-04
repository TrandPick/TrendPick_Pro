package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.ProductOption;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    public static CartItem createCartItem(Cart cart, Product product, CartItemRequest cartItemRequest){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setColor(cartItemRequest.getColor());
        cartItem.setSize(cartItemRequest.getSize());
        cartItem.setCount(cartItemRequest.getCount());
        return cartItem;
    }
    public void addCount(int count){
        this.count += count;
    }
}