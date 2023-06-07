package project.trendpick_pro.domain.cart.entity.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    @Min(value=1,message = "한 개 이상 선택하세요.")
    private int count;

}
