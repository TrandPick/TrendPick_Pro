package project.trendpick_pro.domain.withdraw.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.trendpick_pro.domain.cart.repository.CartItemRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.domain.withdraw.entity.dto.WithDrawApplyForm;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WithdrawApplyRepositoryTest {
    @Autowired
    protected WithdrawApplyRepository withdrawApplyRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @DisplayName("findWithdrawsByStoreName - 해당 스토어명으로 신청한 출금 신청 목록을 모두 조회한다.")
    @Test
    void findWithdrawsByStoreName() {
        //given
        Member member = memberRepository.save(Member.builder()
                .email("brand@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.BRAND_ADMIN)
                .brand("Polo")
                .build());

        WithDrawApplyForm withDrawApplyForm1 = new WithDrawApplyForm("국민은행", "123-456-789", 10000);
        WithDrawApplyForm withDrawApplyForm2 = new WithDrawApplyForm("우리은행", "123-456-789", 10000);
        WithDrawApplyForm withDrawApplyForm3 = new WithDrawApplyForm("하나은행", "123-456-789", 10000);
        withdrawApplyRepository.save(WithdrawApply.of(withDrawApplyForm1, member));
        withdrawApplyRepository.save(WithdrawApply.of(withDrawApplyForm2, member));
        withdrawApplyRepository.save(WithdrawApply.of(withDrawApplyForm3, member));

        //when
        List<WithdrawApply> withdraws = withdrawApplyRepository.findWithdrawsByStoreName("Polo");

        //then
        Assertions.assertThat(withdraws.size()).isEqualTo(3);
    }


}