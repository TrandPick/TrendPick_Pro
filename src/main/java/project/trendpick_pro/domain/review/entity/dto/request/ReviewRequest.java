package project.trendpick_pro.domain.review.entity.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReviewRequest {
    @NotBlank
    private String content;
    private int rating;
}
