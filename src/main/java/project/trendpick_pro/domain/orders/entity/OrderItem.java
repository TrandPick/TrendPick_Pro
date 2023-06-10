package project.trendpick_pro.domain.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.product.entity.Product;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    @Column(name = "count", nullable = false)
    private int count;

    private OrderItem(Product product, int orderPrice, int count) {
        this.product = product;
        this.orderPrice = orderPrice;
        this.count = count;

        product.removeStock(count);
        product.increaseSaleCount(count);
    }

    public static OrderItem of(Product product, OrderItemDto orderItemDto) {
        return new OrderItem(product, orderItemDto.getPrice(), orderItemDto.getCount());
    }

    public void connectOrder(Order order) {
        this.order = order;
    }

    public void cancel() {
        this.product.addStock(count);
        this.product.decreaseSaleCount(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
