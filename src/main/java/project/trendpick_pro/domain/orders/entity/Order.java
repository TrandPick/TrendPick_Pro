package project.trendpick_pro.domain.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.delivery.entity.DeliveryState;
import project.trendpick_pro.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private int totalPrice = 0;

    public void connectUser(Member member) {
        this.member = member;
    }

    public void connectPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void connectDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.connectOrder(this);
    }

    public static Order createOrder(Member member, Delivery delivery, OrderStatus status, List<OrderItem> orderItems, String paymentMethod) {
        Order order = new Order();
        order.connectUser(member);
        order.connectDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
            order.totalPrice += orderItem.getTotalPrice();
        }
        order.status = status;
        order.paymentMethod = paymentMethod;

        return order;
    }

    public static Order createOrder(Member member, Delivery delivery, OrderStatus status, List<OrderItem> orderItems) {
        Order order = new Order();
        order.connectUser(member);
        order.connectDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
            order.totalPrice += orderItem.getTotalPrice();
        }
        order.status = status;
        order.paymentMethod = "";

        return order;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        for (OrderItem orderItem : this.orderItems) {
            orderItem.getProduct().decreaseSaleCount(orderItem.getCount());
            orderItem.getProduct().addStock(orderItem.getCount());
        }
    }
}
