package project.trendpick_pro.domain.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private LocalDateTime payDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_card_id")
    private CouponCard couponCard;
    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    @Column(name = "total_price")
    private int totalPrice;

    @Column(name = "discount_price")
    private int discountPrice;

    @Column(name = "size", nullable = false)
    private String size;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "count", nullable = false)
    private int quantity;

    private OrderItem(Product product,  int quantity, String size, String color) {
        this.product = product;
        if(product.getDiscountedPrice() > 0){
            this.orderPrice = product.getDiscountedPrice();
        }else{
            this.orderPrice = product.getProductOption().getPrice();
        }
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.totalPrice = this.orderPrice * this.quantity;
        this.discountPrice = 0;
        this.payDate=LocalDateTime.now();
//        product.getProductOption().decreaseStock(quantity);
    }

    public void modifyQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static OrderItem of(Product product, OrderItemDto orderItemDto) {
        return new OrderItem(product, orderItemDto.getQuantity(), orderItemDto.getSize(), orderItemDto.getColor());
    }

    public static OrderItem of(Product product, int quantity, String size, String color) {
        return new OrderItem(product, quantity, size, color);
    }

    public static OrderItem of(Product product, CartItem cartItem) {
        return new OrderItem(product, cartItem.getQuantity(), cartItem.getSize(), cartItem.getColor());
    }

    public void connectOrder(Order order) {
        this.order = order;
    }

    public void cancel() {
        this.product.getProductOption().increaseStock(getQuantity());
        this.couponCard.cancel(this);
    }
    public void applyCouponCard(CouponCard couponCard) {
        this.couponCard = couponCard;
    }
    public void cancelCouponCard(){
        this.couponCard = null;
        this.cancelDiscount();
    }

    public void discount(int price){
        this.discountPrice += price;
    }
    private void cancelDiscount(){
        this.discountPrice = 0;
    }
}
