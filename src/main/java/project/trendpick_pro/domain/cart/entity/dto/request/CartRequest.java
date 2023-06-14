package project.trendpick_pro.domain.cart.entity.dto.request;


import jakarta.validation.constraints.NotBlank;

public class CartRequest {
    @NotBlank
    private Long memberId;
}