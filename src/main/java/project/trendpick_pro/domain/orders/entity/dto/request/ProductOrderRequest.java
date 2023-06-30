package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.Data;

@Data
public class ProductOrderRequest {

        private Long productId;

        private int quantity;

        private String size;

        private String color;
}
