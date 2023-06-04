package project.trendpick_pro.domain.review.entity.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReviewSaveRequest {
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @NotBlank(message = "메인으로 사용하실 사진을 입력해주세요")
    private MultipartFile mainFile;

    @NotBlank(message = "추가적으로 사용하실 사진을 입력해주세요")
    private List<MultipartFile> subFiles;

    private int rating;
}
