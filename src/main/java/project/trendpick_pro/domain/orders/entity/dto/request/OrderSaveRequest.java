package project.trendpick_pro.domain.orders.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderSaveRequest {

    private Long productId;

    @NotBlank(message = "수량을 입력해주세요.")
    private int quantity;
    private String color; // 해당 상품 색상

    private String size; // 해당 상품 사이즈
    private int count; // 해당 상품 수량
}
