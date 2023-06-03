package project.trendpick_pro.domain.recommend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommend {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "main_file", nullable = false)
    private String mainFile;

    @Column(name = "price", nullable = false)
    private int price;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Recommend(String name, Brand brand, String mainFile, int price) {
        this.name = name;
        this.brand = brand;
        this.mainFile = mainFile;
        this.price = price;
    }

    public void connectProduct(Product product) {
        this.product = product;
    }

    public void connectMember(Member member) {
        this.member = member;
    }

    public static Recommend of(Product product) {
        return Recommend.builder()
                .name(product.getName())
                .brand(product.getBrand())
                .mainFile(product.getFile().getFileName())
                .price(product.getPrice())
                .build();
    }
}
