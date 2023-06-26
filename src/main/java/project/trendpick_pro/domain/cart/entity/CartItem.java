package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.time.LocalDate;

@Entity
@Getter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "size", nullable = false)
    private String size;

    @Column(name = "color", nullable = false)
    private String color;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate createDate;

    @PrePersist // DB에 INSERT 되기 직전에 실행. 즉 DB에 값을 넣으면 자동으로 실행
    public void createDate() {
        this.createDate = LocalDate.now();
    }

    @Builder
    public CartItem(Cart cart, Product product, int quantity, String size, String color) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
    }
    public static CartItem of(Cart cart, Product product, CartItemRequest cartItemRequest){
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(cartItemRequest.getQuantity())
                .size(cartItemRequest.getSize())
                .color(cartItemRequest.getColor())
                .build();
    }
    public void addCount(int quantity){
        this.quantity += quantity;
    }

    public void update(int quantity){
        this.quantity=quantity;
        this.cart.updateTotalCount();
    }
    public void connectOrder(Order order) {
        this.order = order;
    }

}