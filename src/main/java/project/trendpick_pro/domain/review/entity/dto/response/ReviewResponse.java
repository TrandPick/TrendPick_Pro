package project.trendpick_pro.domain.review.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.Review;

import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {
    private Long id;
    private String username;   //User
    private Long productId;    //Product
    @Lob
    private String content;

    private int rating;

    @Builder
    @QueryProjection
    public ReviewResponse(Long id, String username, Long productId, String content, int rating) {
        this.id = id;
        this.username = username;
        this.productId = productId;
        this.content = content;
        this.rating = rating;
    }

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .username(review.getUsername())
                .productId(review.getProductId())
                .content(review.getContent())
                .rating(review.getRating())
                .build();
    }
}
