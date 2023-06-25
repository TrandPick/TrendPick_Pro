package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDiscountResponse {

    private int price;

    private double discountRate;

    private int discountedPrice;

    @Builder
    @QueryProjection
    public ProductDiscountResponse(double discountRate, int discountedPrice, int price) {
        this.discountRate = discountRate;
        this.discountedPrice = discountedPrice;
        this.price = price;
    }

    public static ProductDiscountResponse of (Product product) {
        return ProductDiscountResponse.builder()
                .discountRate(product.getDiscountRate())
                .discountedPrice(product.getDiscountedPrice())
                .price(product.getPrice())
                .build();
    }
}