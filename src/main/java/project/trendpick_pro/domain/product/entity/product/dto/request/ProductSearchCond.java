package project.trendpick_pro.domain.product.entity.product.dto.request;

import lombok.Getter;
import project.trendpick_pro.domain.product.entity.product.ProductStatus;

@Getter
public class ProductSearchCond {

    private String mainCategory;
    private String subCategory;
    private String keyword;

    public ProductSearchCond(String mainCategory, String subCategory) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
    }

    public ProductSearchCond(String keyword) {
        this.keyword = keyword;
    }
}
