package project.trendpick_pro.domain.product.entity.productOption.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductOptionSaveRequest {

    @NotBlank(message = "사이즈을 선택해주세요.")
    private List<String> sizes;

    @NotBlank(message = "색상을 선택해주세요.")
    private List<String> colors;

    @Min(value = 1, message = "재고 수량을 입력해주세요.(1개 이상부터 입력할 수 있습니다.)")
    private int stock;

    @Min(value = 1, message = "가격을 입력해주세요.(1원 이상부터 입력할 수 있습니다.)")
    private int price;

    @Builder
    public ProductOptionSaveRequest(List<String> sizes, List<String> colors, int stock, int price) {
        this.sizes = sizes;
        this.colors = colors;
        this.stock = stock;
        this.price = price;
    }

    public static ProductOptionSaveRequest of (List<String> sizes, List<String> colors, int stock, int price) {
        return ProductOptionSaveRequest.builder()
                .sizes(sizes)
                .colors(colors)
                .stock(stock)
                .price(price)
                .build();
    }
}
