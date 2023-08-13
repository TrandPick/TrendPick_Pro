package project.trendpick_pro.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.exception.NotificationTypeNotMatchException;
import project.trendpick_pro.domain.orders.entity.Order;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String deliveryState;

    @Builder
    private Notification(Order order, Member member, NotificationType notificationType, String deliveryState) {
        this.order = order;
        this.member = member;
        this.notificationType = notificationType;
        this.deliveryState = deliveryState;
    }

    public static <T> Notification of(Member member, T object){
        if (object instanceof Order) {
            return createOrderNotification(member, (Order) object);
        }
        throw new NotificationTypeNotMatchException("알림 타입이 일치하지 않습니다.");
    }

    public void updateNotificationType(String notificationType, String deliveryState){
        this.notificationType=checkNotificationType(notificationType);
        this.deliveryState=deliveryState;
     }

     public boolean validateNotificationType(String notificationType){
        return Objects.equals(this.notificationType.getValue(), checkNotificationType(notificationType).getValue());
     }

    private static Notification createOrderNotification(Member member, Order order) {
        return Notification.builder()
                .member(member)
                .order(order)
                .notificationType(checkNotificationType(order))
                .deliveryState(order.getDeliveryState())
                .build();
    }

    private static <T> NotificationType checkNotificationType(T object) {
        if (object instanceof Order) {
            return checkOrderNotificationType((Order) object);
        }
        throw new NotificationTypeNotMatchException("알림 타입이 일치하지 않습니다.");
    }

    private static NotificationType checkOrderNotificationType(Order order) {
        if (order.getOrderStatus().getValue().equals("ORDERED")) {
            return NotificationType.ORDERED;
        } else if (order.getOrderStatus().getValue().equals("CANCELED")) {
            return NotificationType.ORDER_CANCELED;
        } else return NotificationType.ORDER_STATE_CHANGED;
    }
}
