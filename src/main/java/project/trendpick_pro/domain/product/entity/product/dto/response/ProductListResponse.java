package project.trendpick_pro.domain.product.entity.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListResponse implements Serializable {

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
        if (product.getDiscountedPrice() == 0 && product.getDiscountRate() == 0) {
            return ProductListResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .brand(product.getBrand().getName())
                    .mainFile(product.getFile().getFileName())
                    .price(product.getProductOption().getPrice())
                    .build();
        } else {
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
    }
}