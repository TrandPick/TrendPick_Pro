package project.trendpick_pro.domain.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.delivery.entity.Delivery;
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
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private String paymentMethod;

    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "total_price", nullable = false)
    private int totalPrice = 0;

    public void connectUser(Member member) {
        this.member = member;
    }

    public void connectPayment(String paymentMethod, String paymentKey) {
        this.paymentMethod = paymentMethod;
        this.paymentKey = paymentKey;
        productDiscount();
    }

    public void connectDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public static Order createOrder(Member member, Delivery delivery, List<OrderItem> orderItems) {
        Order order = new Order();
        order.connectUser(member);
        order.connectDelivery(delivery);
        order.settingOrderItems(orderItems);
        order.orderStatus = OrderStatus.TEMP;
        order.paymentMethod = "";
        return order;
    }

    public void updateStatus(OrderStatus status) {
        this.orderStatus = status;
    }

    public int getTotalDiscountedPrice(){
        int totalDisCountPrice = 0;
        for (OrderItem orderItem : getOrderItems()) {
            totalDisCountPrice += orderItem.getDiscountPrice();
        }
        return totalDisCountPrice;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELED;
        this.delivery.canceledDelivery();
        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel();
        }
    }

    public void cancelTemp() {
        this.orderStatus = OrderStatus.TEMP;
        this.delivery.canceledDelivery();
        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel();
        }
    }

    public String getOrderState(){
        return switch (orderStatus.getValue()){
            case "ORDERED"->"결제완료";
            case "CANCELED"->"주문취소";
            default -> "미결제";
        };
    }

    public String getDeliveryState(){
        return switch (delivery.getState().getValue()){
            case "DELIVERY_ING"->"배송중";
            case "COMPLETED"->"배송완료";
            case "DELIVERY_CANCELED"->"환불신청";
            case "ORDER_CANCELED"->"배송전취소";
            default -> "준비중";
        };
    }

    private void productDiscount(){
        for (OrderItem orderItem : getOrderItems()) {
            this.totalPrice -= orderItem.getDiscountPrice();
        }
    }

    private void settingOrderItems(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            this.addOrderItem(orderItem);
            this.totalPrice += orderItem.getOrderItemByQuantity();
        }
    }

    private void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.connectOrder(this);
    }
}