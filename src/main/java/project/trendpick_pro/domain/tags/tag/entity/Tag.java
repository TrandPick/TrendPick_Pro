package project.trendpick_pro.domain.tags.tag.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.product.entity.product.Product;

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

    public void connectProduct(Product product){
        this.product = product;
    }

    public void disconnectProduct(){
        this.product = null;
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
