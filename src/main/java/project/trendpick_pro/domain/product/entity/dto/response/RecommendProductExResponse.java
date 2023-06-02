package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendProductExResponse {

    private Long productId;
    private String tagName;

    private int totalScore;

    @QueryProjection
    public RecommendProductExResponse(Long productId, String tagName){
        this.productId = productId;
        this.tagName = tagName;
        totalScore = 0;
    }

    public void plusTotalScore(int score){
        this.totalScore += score;
    }

}
