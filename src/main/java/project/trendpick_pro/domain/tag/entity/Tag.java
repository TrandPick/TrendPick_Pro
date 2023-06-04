package project.trendpick_pro.domain.tag.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Tag(String name) {
        this.name = name;
    }
    public Tag(Product product, String name) {
        this.name = name;
        this.product = product;
    }

    public void connectProduct(Product product) {
        this.product = product;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                '}';
    }
}
