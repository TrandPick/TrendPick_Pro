package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductOrderRequest {

        private Long productId;
        private int quantity;
        private String size;
        private String color;

        public ProductOrderRequest(Long productId, int quantity, String size, String color) {
                this.productId = productId;
                this.quantity = quantity;
                this.size = size;
                this.color = color;
        }
}
