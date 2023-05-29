package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.file.CommonFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListResponse {

    private Long id;
    private String name;
    private String brand;
    private String mainFile;
    private int price;

    @QueryProjection
    public ProductListResponse(Long id, String name, String brand, String mainFile, int price) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.mainFile = mainFile;
        this.price = price;
    }
}
