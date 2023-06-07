package project.trendpick_pro.domain.cart.entity.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    @NotBlank(message = "해당 상품의 색상을 입력해주세요.")
    private String color;

    @NotBlank(message ="해당 상품의 사이즈를 입력해주세요.")
    private String size;
    @Min(value=1,message = "한 개 이상 선택하세요.")
    private int count;

}
