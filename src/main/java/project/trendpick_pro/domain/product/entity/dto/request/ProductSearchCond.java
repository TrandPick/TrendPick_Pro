package project.trendpick_pro.domain.product.entity.dto.request;

import lombok.Getter;

@Getter
public class ProductSearchCond {

    private final String mainCategory;
    private final String subCategory;
    private Integer sortCode;

    public ProductSearchCond(String mainCategory, String subCategory, Integer sortCode) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.sortCode = sortCode;
    }

    public ProductSearchCond(String mainCategory, String subCategory) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
    }
}
