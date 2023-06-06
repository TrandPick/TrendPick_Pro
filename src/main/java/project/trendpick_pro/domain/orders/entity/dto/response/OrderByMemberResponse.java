package project.trendpick_pro.domain.orders.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OrderByMemberResponse {

    private String productImage;
    private String brandName;
    private String productName;
    private String size;
    private LocalDateTime orderDate;
    private int totalPrice;
    private String orderStatus;
    private String deliveryStatus;

    @QueryProjection
    public OrderByMemberResponse(String productImage, String brandName, String productName, String size, LocalDateTime orderDate, int totalPrice, String orderStatus, String deliveryStatus) {
        this.productImage = productImage;
        this.brandName = brandName;
        this.productName = productName;
        this.size = size;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.deliveryStatus = deliveryStatus;
    }
}
