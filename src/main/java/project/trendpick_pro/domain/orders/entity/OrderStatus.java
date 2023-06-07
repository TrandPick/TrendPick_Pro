package project.trendpick_pro.domain.orders.entity;

public enum OrderStatus {
    ORDERED("ORDERED"),
    CANCELLED("CANCELLED");

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
