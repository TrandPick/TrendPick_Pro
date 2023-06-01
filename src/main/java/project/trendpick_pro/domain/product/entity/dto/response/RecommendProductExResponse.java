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

    private int totalScore;

    @QueryProjection
    public RecommendProductExResponse(Long productId){
        this.productId = productId;
        totalScore = 0;
    }

}
