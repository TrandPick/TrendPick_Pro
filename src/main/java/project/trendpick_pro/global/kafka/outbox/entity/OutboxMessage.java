package project.trendpick_pro.global.kafka.outbox.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OutboxMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "payload", nullable = false)
    private String payload;

    private String code;

    private String email;

    @OneToMany(mappedBy = "outboxMessage", cascade = CascadeType.PERSIST)
    private List<OrderMaterial> orderMaterials = new ArrayList<>();

    public void disconnectOrderMaterial(){
        this.orderMaterials = null;
    }

    public OutboxMessage (String topic, String message, String payload,
                          List<OrderMaterial> orderMaterial, String code) {
        this.topic = topic;
        this.message = message;
        this.payload = payload;
        this.orderMaterials = orderMaterial;
        this.code = code;
    }

    public OutboxMessage (String topic, String message, String payload, String email) {
        this.topic = topic;
        this.message = message;
        this.payload = payload;
        this.email = email;
    }
}
