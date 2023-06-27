package project.trendpick_pro.domain.product.entity.product.dto.request;

import lombok.Getter;

@Getter
public class ProductSearchCond {

    private String mainCategory;
    private String subCategory;
    private String keyword;
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

    public ProductSearchCond(String keyword) {
        this.keyword = keyword;
    }
}
