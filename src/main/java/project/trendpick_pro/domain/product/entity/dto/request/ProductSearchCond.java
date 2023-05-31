package project.trendpick_pro.domain.product.entity.dto.request;

import lombok.Getter;

@Getter
public class ProductSearchCond {

    private final String mainCategory;
    private final String subCategory;
    private final Integer sortCode;

    public ProductSearchCond(String mainCategory, String subCategory, Integer sortCode) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.sortCode = sortCode;
    }
}
