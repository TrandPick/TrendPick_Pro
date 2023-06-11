package project.trendpick_pro.domain.review.entity.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReviewSaveRequest (
    @NotBlank(message = "제목을 입력해주세요") String title,
    @NotBlank(message = "내용을 입력해주세요") String content,
    @NotNull(message = "점수를 입력해주세요.")int rating){
    @Override
    public String toString() {
        return "ProductSaveRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", rating='" + rating +
                '}';
    }
}
