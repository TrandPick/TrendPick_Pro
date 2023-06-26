package project.trendpick_pro.domain.product.entity.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductByRecommended {

    private Long productId;
    private String tagName;

    private int totalScore;

    @QueryProjection
    public ProductByRecommended(Long productId, String tagName){
        this.productId = productId;
        this.tagName = tagName; //리턴할때는 불필요한 데이터
        totalScore = 0;
    }

    public void plusTotalScore(int score){
        this.totalScore += score;
    }

}
