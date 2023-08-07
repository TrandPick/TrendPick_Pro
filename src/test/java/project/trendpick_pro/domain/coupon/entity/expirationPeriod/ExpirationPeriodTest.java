package project.trendpick_pro.domain.coupon.entity.expirationPeriod;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ExpirationPeriodTest {

    @DisplayName("쿠폰, 쿠폰카드 생성시에 적용되는 만료기간을 생성한다. 제한일이 있을 경우 ISSUE_AFTER_DATE 타입으로 생성된다.")
    @Test
    void createExpirationPeriodISSUE_AFTER_DATE() throws Exception {
        //given
        ExpirationPeriod expirationPeriod = ExpirationPeriod.assignIssueAfterDate(10);

        //when //then
        assertThat(expirationPeriod.getExpirationType()).isEqualTo(ExpirationType.ISSUE_AFTER_DATE);
    }

    @DisplayName("쿠폰, 쿠폰카드 생성시에 적용되는 만료기간을 생성한다. 제한기간이 있을 경우 PERIOD 타입으로 생성된다.")
    @Test
    void createExpirationPeriodPERIOD() throws Exception {
        //given
        LocalDateTime registeredDateTime = LocalDateTime.of(2023, 8, 1, 10, 10, 10);

        ExpirationPeriod expirationPeriod = ExpirationPeriod.assignPeriod(registeredDateTime, registeredDateTime.plusDays(1));

        //when //then
        assertThat(expirationPeriod.getExpirationType()).isEqualTo(ExpirationType.PERIOD);
    }
}