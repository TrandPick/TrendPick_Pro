package project.trendpick_pro.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String orderState;
    private String deliveryState;

    @Builder
    public Notification(Order order, Member member, String orderState, String deliveryState) {
        this.order = order;
        this.member = member;
        this.orderState = orderState;
        this.deliveryState = deliveryState;
    }

    public static Notification of(Member member, Order order){
        return Notification.builder()
                .member(member)
                .order(order)
                .orderState(order.getOrderState())
                .deliveryState(order.getDeliveryState())
                .build();
    }

     public void updateOrderState(String orderState, String deliveryState){
        this.orderState=orderState;
        this.deliveryState=deliveryState;
     }
}
