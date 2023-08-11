package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CartToOrderRequest {
    private List<Long> selectedItems;

    public CartToOrderRequest(List<Long> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
