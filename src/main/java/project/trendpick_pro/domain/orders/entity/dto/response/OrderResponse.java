package project.trendpick_pro.domain.orders.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {
    private Long orderId;
    private Long productId;
    private String productFilePath;
    private String brandName;
    private String productName;
    private int count;
    private LocalDateTime orderDate;
    private LocalDateTime canceledDate;
    private int productPrice;
    private String orderStatus;
    private String deliveryStatus;

    @Builder
    @QueryProjection
    public OrderResponse(Long orderId, Long productId, String productFilePath, String brandName, String productName, int count,int productPrice, LocalDateTime orderDate, LocalDateTime canceledDate,  String orderStatus, String deliveryStatus) {
        this.orderId = orderId;
        this.productId = productId;
        this.productFilePath = productFilePath;
        this.brandName = brandName;
        this.productName = productName;
        this.count = count;
        this.productPrice = productPrice;
        this.orderDate = orderDate;
        this.canceledDate = canceledDate;
        this.orderStatus = orderStatus;
        this.deliveryStatus = deliveryStatus;
    }

    public int getTotalPrice(){
        return productPrice * count;
    }
}
