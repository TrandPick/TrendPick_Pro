package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.orders.entity.OrderStatus;

@Getter
@NoArgsConstructor
public class OrderSearchCond {

    private Long memberId;
    private String brand;
    private String status;

    public OrderSearchCond(Long memberId) {
        this.memberId = memberId;
    }

    public OrderSearchCond(String brand){
        this.brand = brand;
    }

    public OrderSearchCond(Long memberId, OrderStatus status) {
        this.memberId = memberId;
        this.status = status.getValue();
    }


}
