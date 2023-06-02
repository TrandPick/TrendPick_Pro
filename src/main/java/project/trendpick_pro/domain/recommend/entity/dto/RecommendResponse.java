package project.trendpick_pro.domain.recommend.entity.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import project.trendpick_pro.domain.recommend.entity.Recommend;

public record RecommendResponse(Long id, String name, String brand, String mainFile, int price) {

    @Builder
    @QueryProjection
    public RecommendResponse {
    }

    public static RecommendResponse of(Recommend recommend) {
        return RecommendResponse.builder()
                .id(recommend.getProduct().getId())
                .name(recommend.getName())
                .brand(recommend.getBrand().getName())
                .mainFile(recommend.getMainFile())
                .price(recommend.getPrice())
                .build();
    }
}
