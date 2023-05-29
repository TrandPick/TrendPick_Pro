package project.trendpick_pro.domain.product.entity.dto.request;

import lombok.Getter;

@Getter
public class ProductSearchCond {

    private final String mainCategory;
    private final String subCategory;

    public ProductSearchCond(String mainCategory, String subCategory) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
    }
}
