package project.trendpick_pro.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id", nullable = false)
    private MainCategory mainCategory;    // Category

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;   // Category


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private CommonFile file;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "stock", nullable = false)
    private int stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Tag> tags = new ArrayList<>();

    public long reviewCount = 0;
    public double rateAvg = 0;

    @Builder
    public Product(String name, MainCategory mainCategory, SubCategory subCategory, Brand brand,
                   String description, CommonFile file, int price, int stock, List<Tag> tags) {
        this.name = name;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.brand = brand;
        this.description = description;
        this.file = file;
        this.price = price;
        this.stock = stock;
        this.tags = tags;
    }

    public static Product of(ProductSaveRequest request, MainCategory mainCategory
            , SubCategory subCategory, Brand brand,CommonFile file) {
        return Product.builder()
                .name(request.getName())
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .brand(brand)
                .description(request.getDescription())
                .file(file)
                .price(request.getPrice())
                .stock(request.getStock())
                .tags(request.getTags())
                .build();
    }

    public void addStock(int quantity) {
        this.stock += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stock - quantity;
        if (restStock < 0) {
            throw new ProductStockOutException("재고가 부족합니다.");
        }
        this.stock = restStock;
    }

    public void addReview(int rating){
        double total = getRateAvg() * getReviewCount() + rating;

        this.reviewCount++;
        this.rateAvg = Math.round(total / reviewCount * 10) / 10.0;
    }

    public void update(ProductSaveRequest request) {

        // 여기서 파일 지지고 볶고 할 예정
    }


}
