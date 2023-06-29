package project.trendpick_pro.domain.coupon.entity;

import lombok.Getter;

@Getter
public enum CouponStatus {
    USED("USED"), //사용함
    AVAILABLE("AVAILABLE"), //사용가능
    EXPIRED("EXPIRED"), //기간만료 //배치로 하루마다 체크
    NOT_YET_ACTIVE("NOT_YET_ACTIVE"); //아직 비활성화

    private String value;

    CouponStatus(String value) {
        this.value = value;
    }
}
