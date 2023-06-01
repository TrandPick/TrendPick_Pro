package project.trendpick_pro.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

//임시
@Entity
@Getter
@NoArgsConstructor
public class RecommendProductEx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int totalScore;

    public RecommendProductEx(Product product) {
        this.product = product;
    }


}
