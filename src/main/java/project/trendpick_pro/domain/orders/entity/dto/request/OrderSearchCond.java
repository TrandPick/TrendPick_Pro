package project.trendpick_pro.domain.orders.entity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderSearchCond {

    private Long memberId;

    public OrderSearchCond(Long memberId) {
        this.memberId = memberId;
    }
}
