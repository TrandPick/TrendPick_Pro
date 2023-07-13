package project.trendpick_pro.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.orders.entity.Order;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    private String address;

    @Enumerated(EnumType.STRING)
    private DeliveryState state;

    public Delivery(String address){
        this.address = address;
        state = DeliveryState.READY;
    }

    public void updateStatus(String status){
        state = DeliveryState.valueOf(status);
    }

    public void canceledDelivery(){
        state = DeliveryState.ORDER_CANCELED;
    }
}
