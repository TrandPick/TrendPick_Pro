package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartToOrderRequest {
    private List<Long> selectedItems;
}
