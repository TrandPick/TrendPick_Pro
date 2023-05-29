package project.trendpick_pro.domain.product.entity.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductOptionSaveRequest {
    @NotBlank(message = "상품색상을 선택해주세요.")
    private String color;

    @NotBlank(message = "사이즈를 선택해주세요.")
    private String size;

    @Min(1)
    private int count;
}
