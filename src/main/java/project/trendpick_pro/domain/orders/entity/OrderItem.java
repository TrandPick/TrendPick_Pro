package project.trendpick_pro.domain.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    @Column(name = "size", nullable = false)

    private String size;

    private OrderItem(Product product,String size, int orderPrice, int count) {
        this.product = product;
        this.orderPrice = orderPrice;
        this.count = count;
        this.size = size;

        product.removeStock(count);
    }

    public static OrderItem of(Product product, OrderItemDto orderItemDto) {
        return new OrderItem(product, orderItemDto.getSize(), orderItemDto.getPrice(), orderItemDto.getCount());
    }

    public void connectOrder(Order order) {
        this.order = order;
    }

    public void cancel() {
        this.product.addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
