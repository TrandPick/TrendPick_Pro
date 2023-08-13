package project.trendpick_pro.domain.notification.entity;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum NotificationType {

    ORDERED("주문완료"),
    ORDER_CANCELED("주문취소"),
    ORDER_STATE_CHANGED("주문상태변경"),
    DELIVERY_STATE_CHANGED("배송상태변경");

    private String value;

    NotificationType(String value) {
        this.value = value;
    }

    public static boolean isType(String value){
        return Stream.of(values())
                .anyMatch(notificationType -> notificationType.getValue().equals(value));
    }
}
