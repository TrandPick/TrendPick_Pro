package project.trendpick_pro.domain.tag.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.tag.entity.type.TagType;

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

    private int score; //선호도 점수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public void increaseScore(TagType type){
        //유형에 따라 가중치
        switch (type){
            case ORDER: score += 10;
                break;
            case CART: score += 5;
                break;
            default: score += 1;
                break;
        }
    }

    public void decreaseScore(){

    }
}
