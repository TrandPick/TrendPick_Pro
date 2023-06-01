package project.trendpick_pro.domain.tag.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public int score; //선호도 점수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public void increaseScore(int type){
        //유형에 따라 가중치
        switch (type){
            case 1 : score += 10;
                break;
            case 2: score += 7;
                break;
            case 3: score += 5;
                break;
            default: score += 1;
                break;
        }
    }

    public void decreaseScore(){

    }
}
