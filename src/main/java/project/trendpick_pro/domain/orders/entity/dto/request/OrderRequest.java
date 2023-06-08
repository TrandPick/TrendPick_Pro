package project.trendpick_pro.domain.orders.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderRequest {

    private Long productId;

    @NotBlank(message = "수량을 입력해주세요.")
    private int count;
}
