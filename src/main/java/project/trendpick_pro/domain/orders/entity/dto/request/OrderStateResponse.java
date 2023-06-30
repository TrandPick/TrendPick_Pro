package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStateResponse {
    String message;
    Long orderId;
}
