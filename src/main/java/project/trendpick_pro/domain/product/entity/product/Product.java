package project.trendpick_pro.domain.product.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.recommend.entity.Recommend;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private CommonFile file;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductOption productOption;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recommend> recommendList;

    private int reviewCount = 0;
    private double rateAvg = 0;
    private int saleCount = 0;
    private int askCount = 0;

    private double discountRate;

    private int discountedPrice;

    @Builder
    public Product(String name, MainCategory mainCategory, SubCategory subCategory, Brand brand,
                   String description, CommonFile file, ProductOption productOption, Set<Tag> tags) {
        this.name = name;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.brand = brand;
        this.description = description;
        this.file = file;
        this.productOption = productOption;
        this.tags = tags;
    }

    public static Product of(ProductSaveRequest request, MainCategory mainCategory, SubCategory subCategory, Brand brand,
                             CommonFile file, ProductOption productOption) {
        return Product.builder()
                .name(request.getName())
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .brand(brand)
                .description(request.getDescription())
                .file(file)
                .productOption(productOption)
                .build();
    }

    public void update(ProductSaveRequest request, ProductOptionSaveRequest optionSaveRequest, CommonFile file) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.productOption.update(optionSaveRequest);
        this.file = file;
    }

    public void disconnectFile(){
        this.file = null;
    }

    public void increaseAskCount(){
        this.askCount++;
    }
    public void decreaseAskCount(){
        this.askCount--;
    }

    public void increaseSaleCount(int quantity){
        this.saleCount += quantity;
    }

    public void decreaseSaleCount(int quantity){
        this.saleCount -= quantity;
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

    public void addTag(Set<Tag> tags){
        this.tags = tags;
        for (Tag tag : tags) {
            tag.connectProduct(this);
        }
    }

    public void modifyTag(Set<Tag> tags){
        for (Tag tag : this.tags) {
            tag.disconnectProduct();
        }

        this.tags = tags;
        for (Tag tag : tags) {
            tag.connectProduct(this);
        }
    }
}