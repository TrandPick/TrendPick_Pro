package project.trendpick_pro.domain.cart.entity.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import project.trendpick_pro.domain.product.entity.ProductOption;

public class CartRequest {
    @NotBlank
    private Long cartItemId;
    @NotBlank
    private Long productOptionId;
    @Min(1)
    private int count;
}
