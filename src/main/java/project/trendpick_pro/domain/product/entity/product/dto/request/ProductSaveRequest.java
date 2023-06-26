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

//    @NotBlank(message = "메인 카테고리를 정하세요.")
    private String mainCategory;

//    @NotBlank(message = "서브 카테고리를 정하세요.")
    private String subCategory;

//    @NotBlank(message = "브랜드를 입력해주세요.")
    private String brand;

    @NotEmpty(message = "포함될 태그들을 추가해주세요.")
    private List<String> tags;
}