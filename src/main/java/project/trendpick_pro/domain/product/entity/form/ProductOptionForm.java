package project.trendpick_pro.domain.product.entity.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOptionForm {
    @NotBlank(message = "상품 id가 확인되지 않습니다.")
    private Long productId;
    @Min(value = 1, message = "최소 1개 이상은 선택하셔야 합니다.")
    private int quantity;
}
