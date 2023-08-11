package project.trendpick_pro.domain.product.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true, updatable = false   )
    private String productCode;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ProductOption productOption;

    private int reviewCount = 0;
    private double rateAvg = 0;

    private int saleCount = 0;
    private int askCount = 0;

    private double discountRate;
    private int discountedPrice;

    @Builder
    private Product(String productCode, String title, String description) {
        this.productCode = productCode;
        this.title = title;
        this.description = description;
    }

    public static Product of(String title, String description) {
        return Product.builder()
                .productCode("P" + UUID.randomUUID().toString())
                .title(title)
                .description(description)
                .build();
    }

    public void connectProductOption(ProductOption productOption) {
        this.productOption = productOption;
    }

    public void update(ProductSaveRequest request, ProductOptionSaveRequest optionSaveRequest) {
        this.title = request.getName();
        this.description = request.getDescription();
        this.productOption.update(optionSaveRequest);
    }

    public void addReview(int rating){
        double total = getRateAvg() * getReviewCount() + rating;
        this.reviewCount++;
        this.rateAvg = Math.round(total / reviewCount * 10) / 10.0;
    }

    public void applyDiscount(double discountRate) {
        if (discountRate == 0) {
            this.discountRate = 0;
            this.discountedPrice = 0;
        } else {
            this.discountRate = discountRate;
            this.discountedPrice = (int) (this.productOption.getPrice() * (1 - discountRate / 100));
        }
    }

    public void updateTags(Set<Tag> tags){
        for (Tag tag : this.tags) {
            tag.disconnectProduct();
        }
        this.tags = tags;
        for (Tag tag : tags) {
            tag.connectProduct(this);
        }
    }
}