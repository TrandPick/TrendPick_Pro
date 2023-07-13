package project.trendpick_pro.domain.delivery.entity;

import lombok.Getter;

@Getter
public enum DeliveryState {
    READY("READY"),
    DELIVERY_ING("DELIVERY_ING"),
    COMPLETED("COMPLETED"),
    CANCELED("CANCELED"),
    ORDER_CANCELED("ORDER_CANCELED");

    private String value;

    DeliveryState(String value){this.value=value;}
}
