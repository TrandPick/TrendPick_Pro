package project.trendpick_pro.domain.rebate.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RebateOrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rebate_order_item_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "coupon_card_id")
    private CouponCard couponCard;
    @Column(name = "total_price")
    private int totalPrice; //전체금액 (할인제외)
    @Column(name = "order_price", nullable = false)
    private int orderPrice; //주문금액 (할인 계산된 금액)
    @Column(name = "discount_price")
    private int discountPrice; //할인 받은 금액

    // 상품
    @Column(name = "product_subject", nullable = false)
    private String productSubject;
    @Column(name = "size", nullable = false)
    private String size;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "quantity", nullable = false)
    private int quantity;
    @OneToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashLog rebateCashLog; // 정산에 관련된 환급지급내역
    private LocalDateTime rebateDate;

    // 주문품목
    private LocalDateTime orderItemCreateDate;

    // 구매자 회원
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member buyer;
    private String buyerName;

    // 판매자 회원
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Brand seller;
    private String sellerName;

    public RebateOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
        this.order = orderItem.getOrder();
        this.product = orderItem.getProduct();
        this.couponCard = orderItem.getCouponCard();
        this.orderPrice = orderItem.getOrderPrice();
        this.totalPrice = orderItem.getTotalPrice();
        this.discountPrice = orderItem.getDiscountPrice();
        this.size = orderItem.getSize();
        this.color = orderItem.getColor();
        this.quantity = orderItem.getQuantity();
        // 상품 추가 데이터
        this.productSubject = orderItem.getProduct().getTitle();

        // 주문 품목 추가데이터
        this.orderItemCreateDate = orderItem.getOrder().getCreatedDate();

        // 구매자 추가 데이터
        this.buyer = orderItem.getOrder().getMember();
        this.buyerName = orderItem.getOrder().getMember().getUsername();

        // 판매자 추가 데이터
        this.seller = orderItem.getProduct().getProductOption().getBrand();
        this.sellerName = orderItem.getProduct().getProductOption().getBrand().getName();
    }

    public int calculateRebatePrice() {
        return totalPrice - (int) (totalPrice * 0.05); // 정산금액 수수료 5%(임의) 제외하고 계산
    }
    public boolean isAlreadyRebated() {
        if (rebateDate == null)
            return false;
        return true;
    }

    public void setRebateDone(CashLog cashLog) {
        rebateDate = LocalDateTime.now();
        this.rebateCashLog = cashLog;
    }

    public boolean isRebateDone() {
        return rebateDate != null;
    }

    public RebateOrderItem updateWith(RebateOrderItem item) {
        this.orderItem = item.getOrderItem();
        this.order = item.getOrder();
        this.product = item.getProduct();
        this.couponCard = item.getCouponCard();
        this.orderPrice = item.getOrderPrice();
        this.totalPrice = item.getTotalPrice();
        this.discountPrice = item.getDiscountPrice();
        this.size = item.getSize();
        this.color = item.getColor();
        this.quantity = item.getQuantity();
        this.productSubject = item.getProduct().getTitle();
        this.orderItemCreateDate = item.getOrder().getCreatedDate();
        this.buyer = item.getOrder().getMember();
        this.buyerName = item.getOrder().getMember().getUsername();
        this.seller = item.getProduct().getProductOption().getBrand();
        this.sellerName = item.getProduct().getProductOption().getBrand().getName();
        return this;
    }
}


