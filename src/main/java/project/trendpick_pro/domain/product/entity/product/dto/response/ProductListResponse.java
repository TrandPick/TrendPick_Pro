package project.trendpick_pro.domain.product.entity.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import project.trendpick_pro.domain.product.entity.product.Product;
//import project.trendpick_pro.global.search.entity.ProductSearch;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListResponse {

    private Long id;
    private String name;
    private String brand;
    private String mainFile;
    private int price;

    private int discountRate;

    private int discountedPrice;

    @Builder
    @QueryProjection
    public ProductListResponse(Long id, String name, String brand, String mainFile, int price, double discountRate, int discountedPrice) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.mainFile = mainFile;
        this.price = price;
        this.discountRate = (int) discountRate;
        this.discountedPrice = discountedPrice;
    }

    public static ProductListResponse of(Product product) {
        return ProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand().getName())
                .mainFile(product.getFile().getFileName())
                .price(product.getProductOption().getPrice())
                .discountedPrice(product.getDiscountedPrice())
                .discountRate(product.getDiscountRate())
                .build();
    }

//        public static ProductListResponse of(ProductSearch product) {
//        return ProductListResponse.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .brand(product.getBrand())
//                .price(product.getPrice())
//                .build();
//    }
}