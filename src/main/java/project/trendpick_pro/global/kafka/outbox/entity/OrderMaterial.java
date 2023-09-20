package project.trendpick_pro.global.kafka.outbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.cart.entity.CartItem;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class OrderMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Integer quantity;
    private String size;
    private String color;
    private String code;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "outbox_message_id")
    private OutboxMessage outboxMessage;

    public OrderMaterial(Long productId, Integer quantity, String size, String color, String code) {
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.code = code;
    }

    public OrderMaterial(CartItem cartItem, String code){
        this.productId = cartItem.getProduct().getId();
        this.quantity =  cartItem.getQuantity();
        this.size = cartItem.getSize();
        this.color =  cartItem.getColor();
        this.code = code;
    }
}
