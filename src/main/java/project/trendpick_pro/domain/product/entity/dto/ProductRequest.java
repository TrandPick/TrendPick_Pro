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

    public ProductRequest(ProductSaveRequest request1, ProductOptionSaveRequest request2) {
        this.request1 = request1;
        this.request2 = request2;
    }

    public ProductSaveRequest getRequest1() {
        return request1;
    }

    public void setRequest1(ProductSaveRequest request1) {
        this.request1 = request1;
    }

    public ProductOptionSaveRequest getRequest2() {
        return request2;
    }

    public void setRequest2(ProductOptionSaveRequest request2) {
        this.request2 = request2;
    }
}
