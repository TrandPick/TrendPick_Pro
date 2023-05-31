package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.ProductOption;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class  ProductOptionResponse {
    Product product;
    private String color;

    private String size;

    private int count;

    @Builder
    @QueryProjection
    public ProductOptionResponse(Product product, String color, String size,int count) {
        this.product=product;
        this.color=color;
        this.size=size;
        this.count=count;
    }

    public static ProductOptionResponse of (ProductOption productOption) {
        return ProductOptionResponse.builder()
                .product(productOption.getProduct())
                .color(productOption.getColor())
                .size(productOption.getSize())
                .count(productOption.getCount())
                .build();
    }
}
