package project.trendpick_pro.domain.product.entity;

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
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
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
    private MainCategory mainCategory;    // Category

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;   // Category


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
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
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Recommend recommends;


    private int reviewCount = 0;
    private double rateAvg = 0;
    private int saleCount = 0;
    private int askCount = 0;

    @Builder
    public Product(String name, MainCategory mainCategory, SubCategory subCategory, Brand brand,
                   String description, CommonFile file, int price, int stock, Set<Tag> tags) {
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

    public void increaseSaleCount(int quantity){
        this.saleCount += quantity;
    }
    public void decreaseSaleCount(int quantity){
        this.saleCount -= quantity;
    }

    public void increaseAskCount(){
        this.askCount += 1;
    }
    public void decreaseAskCount(){
        this.askCount -= 1;
    }

    public void addReview(int rating){
        double total = getRateAvg() * getReviewCount() + rating;

        this.reviewCount++;
        this.rateAvg = Math.round(total / reviewCount * 10) / 10.0;
    }



    public void update(ProductSaveRequest request, CommonFile file) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.price = request.getPrice();
        this.stock = request.getStock();
        this.file = file;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }

    public void addTag(Set<Tag> tags){
        this.tags = tags;
        for (Tag tag : tags) {
            tag.connectProduct(this);
        }
    }
}
