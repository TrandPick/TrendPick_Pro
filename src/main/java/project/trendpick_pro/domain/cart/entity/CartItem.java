package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.product.entity.Product;

import java.time.LocalDate;

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

    private int quantity; // 해당 상품 수량

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate createDate;

    @PrePersist // DB에 INSERT 되기 직전에 실행. 즉 DB에 값을 넣으면 자동으로 실행
    public void createDate() {
        this.createDate = LocalDate.now();
    }

    @Builder
    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }
    public static CartItem of(Cart cart, Product product, CartItemRequest cartItemRequest){
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(cartItemRequest.getQuantity())
                .build();
    }
    public void addCount(int quantity){
        this.quantity += quantity;
    }

    public void update(int quantity){
        this.quantity=quantity;
        this.cart.updateTotalCount();
    }
}