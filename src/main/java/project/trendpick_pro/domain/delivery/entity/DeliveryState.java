package project.trendpick_pro.domain.delivery.entity;

import lombok.Getter;

@Getter
public enum DeliveryState {
    READY("READY"),
    DELIVERY_ING("DELIVERY_ING"),
    COMPLETED("COMPLETED"),
    CANCELED("CANCELED"),
    ORDER_CANCELED("ORDER_CANCELED");

    // 준비, 배송중, 배송완료,환불신청, 배송전취소

    private String value;

    DeliveryState(String value){this.value=value;}
}
