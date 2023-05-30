package project.trendpick_pro.domain.review.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReviewUpdateRequest {
    private String title;
    private String content;
    private MultipartFile mainFile;
    private List<MultipartFile> subFiles;
    private int rating;

}
