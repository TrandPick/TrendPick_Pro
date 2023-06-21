package project.trendpick_pro.domain.orders.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    TEMP("TEMP"),
    ORDERED("ORDERED"),
    CANCELED("CANCELED");

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
