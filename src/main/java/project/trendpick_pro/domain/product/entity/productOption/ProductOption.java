package project.trendpick_pro.domain.product.entity.productOption;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.trendpick_pro.domain.product.entity.product.ProductStatus;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_option_sizes", joinColumns = @JoinColumn(name = "product_option_id"))
    @Column(name = "size")
    private List<String> sizes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_option_colors", joinColumns = @JoinColumn(name = "product_option_id"))
    @Column(name = "color")
    private List<String> colors;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    public void connectStatus(ProductStatus status){
        this.status = status;
    }

    @Builder
    private ProductOption(List<String> size, List<String> color, int stock, int price) {
        this.sizes = size;
        this.colors = color;
        this.stock = stock;
        this.price = price;
    }

    public static ProductOption of (ProductOptionSaveRequest request) {
        return ProductOption.builder()
                .size(request.getSizes())
                .color(request.getColors())
                .stock(request.getStock())
                .price(request.getPrice())
                .build();
    }

    public void update(ProductOptionSaveRequest request) {
        this.sizes = request.getSizes();
        this.colors = request.getColors();
        this.stock = request.getStock();
        this.price = request.getPrice();
    }

    public void decreaseStock(int quantity) {
        int restStock = this.stock - quantity;
        if (restStock < 0) {
            this.status = ProductStatus.SOLD_OUT;
            throw new ProductStockOutException("재고가 부족합니다.");
        }
        this.stock = restStock;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }
}
