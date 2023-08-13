package project.trendpick_pro.domain.notification.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTypeTest {

    private static Stream<Arguments> CheckingNotificationType() {
        return Stream.of(
                Arguments.of(NotificationType.ORDERED, true),
                Arguments.of(NotificationType.ORDER_CANCELED, true),
                Arguments.of(NotificationType.ORDER_STATE_CHANGED, true)
        );
    }

    @DisplayName("입력된 NotificationType 은 지정된 값만 가질 수 있다.")
    @MethodSource("CheckingNotificationType")
    @ParameterizedTest
    void containsNotificationType(NotificationType notificationType, boolean expected) throws Exception {
        //when
        boolean result = NotificationType.isType(notificationType.getValue());

        //then
        assertThat(result).isEqualTo(expected);
    }

}