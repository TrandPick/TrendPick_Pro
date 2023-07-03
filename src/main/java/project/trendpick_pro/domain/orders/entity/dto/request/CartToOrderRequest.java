package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CartToOrderRequest {
    private List<Long> selectedItems;
}
