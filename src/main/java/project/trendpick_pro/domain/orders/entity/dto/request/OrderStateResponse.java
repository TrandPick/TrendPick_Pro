package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderStateResponse {
    private String message;
    private Long orderId;
    private String email;

    @Builder
    private OrderStateResponse(String message, Long orderId, String email) {
        this.message = message;
        this.orderId = orderId;
        this.email = email;
    }
}
