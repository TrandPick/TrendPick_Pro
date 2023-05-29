package project.trendpick_pro.domain.cart.entity.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CartItemRequest {
    @NotBlank
    private Long cartId;
    @NotBlank
    private Long productOptionId;
    @Min(1)
    private int count;
}
