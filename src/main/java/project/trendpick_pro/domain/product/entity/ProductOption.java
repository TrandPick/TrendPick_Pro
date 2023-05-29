package project.trendpick_pro.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.product.entity.dto.request.ProductOptionSaveRequest;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn Product product;

    private String color; // 색상

    private  String size; // 사이즈
    private int count; // 수량

    @Builder
    public ProductOption(Product product,String color, String size, int count){
        this.product = product;
        this.color = color;
        this.size = size;
        this.count = count;
    }

    public static ProductOption of(Product product,ProductOptionSaveRequest request) {
        return ProductOption.builder()
                .product(product)
                .color(request.getColor())
                .size(request.getSize())
                .count(request.getCount())
                .build();
    }
}
