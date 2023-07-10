package project.trendpick_pro.domain.product.entity.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {

    SALE("SALE"),
    SOLD_OUT("SOLD_OUT"),
    STOP("STOP");

    private String text;

}
