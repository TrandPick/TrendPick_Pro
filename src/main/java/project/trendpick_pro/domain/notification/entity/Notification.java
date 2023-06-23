package project.trendpick_pro.domain.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Notification {
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
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate notificationDate;

    @PrePersist
    public void createDate() {
        this.notificationDate = LocalDate.now();
    }

    public boolean isRead(){
        return notificationDate!=null;
    }

    public static Notification of(Member member,Order order){
        return Notification.builder()
                .member(member)
                .order(order)
                .orderState(order.getOrderState())
                .deliveryState(order.getDeliveryState())
                .build();
    }

}
