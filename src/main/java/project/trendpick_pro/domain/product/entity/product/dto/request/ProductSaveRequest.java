package project.trendpick_pro.domain.product.entity.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaveRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String name;

    @NotBlank(message = "내용을 입력해주세요.")
    private String description;

    private String mainCategory;

    private String subCategory;

    private String brand;

    @NotEmpty(message = "포함될 태그들을 추가해주세요.")
    private List<String> tags;
}