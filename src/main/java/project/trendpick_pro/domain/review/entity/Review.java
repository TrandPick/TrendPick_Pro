package project.trendpick_pro.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;   //User

//    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "product_id")
    private Long productId;    //Product

    @Lob
    private String content;

    private int rating;

    public static Review of(String username, Long productId, ReviewRequest reviewRequest) {
        return Review.builder()
                .username(username)
                .productId(productId)
                .content(reviewRequest.getContent())
                .rating(reviewRequest.getRating())
                .build();
    }

    public void update(ReviewRequest reviewRequest) {
        this.content = reviewRequest.getContent();
        this.rating = reviewRequest.getRating();
    }
}
