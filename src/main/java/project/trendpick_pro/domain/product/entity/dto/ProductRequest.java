package project.trendpick_pro.domain.product.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;

@Data
@NoArgsConstructor
public class ProductRequest {

    private ProductSaveRequest request1;
    private ProductOptionSaveRequest request2;
}
