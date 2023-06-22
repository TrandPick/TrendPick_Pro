package project.trendpick_pro.domain.coupon.entity;

public enum CouponStatus {
    USED("USED"),
    UNUSED("UNUSED"),
    EXPIRED("EXPIRED");

    private String value;

    CouponStatus(String value) {
        this.value = value;
    }
}
