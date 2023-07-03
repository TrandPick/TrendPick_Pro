package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateResponse {
    private String message;
    private Long orderId;
    private String email;
}
