package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.product.entity.Product;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListResponse {

    private Long id;
    private String name;
    private String brand;
    private String mainFile;
    private int price;

    @Builder
    @QueryProjection
    public ProductListResponse(Long id, String name, String brand, String mainFile, int price) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.mainFile = mainFile;
        this.price = price;
    }

    public static ProductListResponse of(Product product) {
        return ProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand().getName())
                .mainFile(product.getFile().getFileName())
                .price(product.getPrice())
                .build();
    }
}