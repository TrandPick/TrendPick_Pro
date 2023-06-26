package project.trendpick_pro.domain.product.entity.productOption;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "product_option_sizes", joinColumns = @JoinColumn(name = "product_option_id"))
    @Column(name = "size")
    private List<String> sizes;

    @ElementCollection
    @CollectionTable(name = "product_option_colors", joinColumns = @JoinColumn(name = "product_option_id"))
    @Column(name = "color")
    private List<String> colors;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "price", nullable = false)
    private int price;

    @Builder
    public ProductOption(List<String> size, List<String> color, int stock, int price) {
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
            throw new ProductStockOutException("재고가 부족합니다.");
        }
        this.stock = restStock;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }
}
