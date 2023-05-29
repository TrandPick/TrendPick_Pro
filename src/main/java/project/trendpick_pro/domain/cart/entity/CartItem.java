package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;
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
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;

    private int count; // 해당 상품 수량

    private int price; // 해당 상품 금액

    @Builder
    public CartItem(Cart cart,  ProductOption productOption, int count) {
        this.cart = cart;
        this.productOption = productOption;
        this.count = count;
        this.price = productOption.getProduct().getPrice();
    }

}