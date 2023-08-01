package project.trendpick_pro.domain.product.entity.productOption;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.product.entity.product.ProductStatus;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;

import java.io.Serializable;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private CommonFile file;

    @Builder
    private ProductOption(List<String> size, List<String> color, int stock, int price) {
        this.sizes = size;
        this.colors = color;
        this.stock = stock;
        this.price = price;
    }

    public static ProductOption of(ProductOptionSaveRequest request) {
        return ProductOption.builder()
                .size(request.getSizes())
                .color(request.getColors())
                .stock(request.getStock())
                .price(request.getPrice())
                .build();
    }

    public void settingConnection(Brand brand, MainCategory mainCategory, SubCategory subCategory, CommonFile file, ProductStatus status) {
        this.brand = brand;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.file = file;
        this.status = status;
    }

    public void update(ProductOptionSaveRequest request) {
        this.sizes = request.getSizes();
        this.colors = request.getColors();
        this.stock = request.getStock();
        this.price = request.getPrice();
    }

    public void updateFile(CommonFile file) {
        this.file = file;
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
