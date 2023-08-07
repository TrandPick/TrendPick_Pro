package project.trendpick_pro.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRoleTypeTest {

    private static Stream<Arguments> CheckingMemberType() {
        return Stream.of(
                Arguments.of(MemberRoleType.MEMBER, true),
                Arguments.of(MemberRoleType.ADMIN, true),
                Arguments.of(MemberRoleType.BRAND_ADMIN, true)
        );
    }

    @DisplayName("멤버 타입이 정의 되어있는 타입인지 체크한다.")
    @MethodSource("CheckingMemberType")
    @ParameterizedTest
    void containsMemberType(MemberRoleType memberRoleType, boolean expected) throws Exception {
        //when
        boolean result = MemberRoleType.isType(memberRoleType);

        //then
        assertThat(result).isEqualTo(expected);
    }
}