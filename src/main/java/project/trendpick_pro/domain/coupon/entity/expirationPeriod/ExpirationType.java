package project.trendpick_pro.domain.coupon.entity.expirationPeriod;

import lombok.Getter;

import java.util.List;

@Getter
public enum ExpirationType {
    PERIOD("PERIOD"),  //oo일 부터 oo일
    ISSUE_AFTER_DATE("ISSUE_AFTER_DATE"); //받은 날로부터 oo일

    private String value;

    ExpirationType(String value) {
        this.value = value;
    }

    public static boolean isType(ExpirationType expirationType) {
        return List.of(values()).contains(expirationType);
    }
}
