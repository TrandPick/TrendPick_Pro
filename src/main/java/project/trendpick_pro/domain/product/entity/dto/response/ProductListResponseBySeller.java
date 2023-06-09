package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListResponseBySeller {

    private Long id;
    private String name;
    private String mainFile;
    private int price;
    private int stock;
    private LocalDateTime createdDate;
    private int saleCount;

    private double rateAvg;
    private int reviewCount;
    private int ask;

    @Builder
    @QueryProjection
    public ProductListResponseBySeller(Long id, String name, String mainFile, int price, int stock, LocalDateTime createdDate, int saleCount, double rateAvg, int reviewCount, int ask) {
        this.id = id;
        this.name = name;
        this.mainFile = mainFile;
        this.price = price;
        this.stock = stock;
        this.createdDate = createdDate;
        this.saleCount = saleCount;
        this.rateAvg = rateAvg;
        this.reviewCount = reviewCount;
        this.ask = ask;
    }
}
