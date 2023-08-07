package project.trendpick_pro.domain.coupon.entity.expirationPeriod;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ExpirationTypeTest {

    private static Stream<Arguments> provideCouponTypeCheckingExpirationType() {
        return Stream.of(
                Arguments.of(ExpirationType.PERIOD, true),
                Arguments.of(ExpirationType.ISSUE_AFTER_DATE, true)
        );
    }

    @DisplayName("쿠폰 타입이 ExpirationType 관련 타입인지 체크한다.")
    @MethodSource("provideCouponTypeCheckingExpirationType")
    @ParameterizedTest
    void containsExpirationType(ExpirationType expirationType, boolean expected) throws Exception {
        //when
        boolean result = ExpirationType.isType(expirationType);

        //then
        assertThat(result).isEqualTo(expected);
    }

}