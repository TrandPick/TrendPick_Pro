package project.trendpick_pro.domain.product.entity.dto.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.product.entity.Product;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendProductExResponse {

    private Product productId;

    private int totalScore;

}
