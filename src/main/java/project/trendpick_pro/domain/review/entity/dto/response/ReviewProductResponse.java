package project.trendpick_pro.domain.review.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.review.entity.Review;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewProductResponse {
    private Long id;
    private String writer;   //User
    private String title;

    private String mainFile;
    private String content;
    private int rating;

    @Builder
    @QueryProjection
    public ReviewProductResponse(Long id, String writer, String title, String content, String mainFile,
                                 int rating) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.mainFile = mainFile;
        this.rating = rating;
    }

    public static ReviewProductResponse of(Review review) {
        return ReviewProductResponse.builder()
                .id(review.getId())
                .writer(review.getWriter())
                .title(review.getTitle())
                .content(review.getContent())
                .mainFile(review.getFile().getFileName())
                .rating(review.getRating())
                .build();
    }

    public static ReviewProductResponse of(String msg) {
        return ReviewProductResponse.builder()
                .content(msg)
                .build();
    }
}