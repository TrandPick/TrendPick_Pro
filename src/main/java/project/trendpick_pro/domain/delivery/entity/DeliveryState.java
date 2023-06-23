package project.trendpick_pro.domain.delivery.entity;

import lombok.Getter;

@Getter
public enum DeliveryState {
    READY("READY"),
    DELIVERY_ING("DELIVERY_ING"),
    COMPLETED("COMPLETED"),
    CANCELED("CANCELED");
    //준비,배송중,배송완료

    private String value;

    DeliveryState(String value){this.value=value;}
}
