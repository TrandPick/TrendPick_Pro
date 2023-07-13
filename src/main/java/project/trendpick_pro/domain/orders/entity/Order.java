package project.trendpick_pro.domain.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.cart.entity.CartItem;
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

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private int totalPrice = 0;

    private String paymentKey;

    public void connectPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

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
    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
    }
    public static Order createOrder(Member member, Delivery delivery, OrderStatus status, List<OrderItem> orderItems, List<CartItem> cartItems) {
        Order order = new Order();
        order.connectUser(member);
        order.connectDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
            order.totalPrice += orderItem.getTotalPrice();
        }
        for (CartItem cartItem : cartItems) {
            order.addCartItem(cartItem);
        }
        order.status = status;
        order.paymentMethod = "";

        return order;
    }

    public static Order createOrder(Member member, Delivery delivery, OrderStatus status, OrderItem orderItem) {
        Order order = new Order();

        order.connectUser(member);
        order.connectDelivery(delivery);
        order.addOrderItem(orderItem);

        order.totalPrice += orderItem.getTotalPrice();
        order.status = status;
        order.paymentMethod = "";

        return order;
    }

    public void modifyStatus(OrderStatus status) {
        this.status = status;
    }

    public int getTotalDiscountedPrice(){
        int totalDisCountPrice = 0;
        for (OrderItem orderItem : getOrderItems()) {
            totalDisCountPrice += orderItem.getDiscountPrice();
        }
        return totalDisCountPrice;
    }

    public String getOrderState(){
        return switch (status.getValue()){
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

    public void cancel() {
        this.status = OrderStatus.CANCELED;
        this.delivery.canceledDelivery();
        for (OrderItem orderItem : this.orderItems) {
            orderItem.getProduct().getProductOption().increaseStock(orderItem.getQuantity());
        }
    }

    public void cancelTemp() {
        this.status = OrderStatus.TEMP;
        this.delivery.canceledDelivery();
        for (OrderItem orderItem : this.orderItems) {
            orderItem.getProduct().getProductOption().increaseStock(orderItem.getQuantity());
        }
    }

    public void updateWithPayment(){
        for (OrderItem orderItem : getOrderItems()) {
            this.totalPrice -= orderItem.getDiscountPrice();
        }
    }
}