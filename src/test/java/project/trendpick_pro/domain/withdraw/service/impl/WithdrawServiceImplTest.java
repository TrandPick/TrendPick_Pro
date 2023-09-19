package project.trendpick_pro.domain.withdraw.service.impl;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.service.CashService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.domain.withdraw.entity.dto.WithDrawApplyForm;
import project.trendpick_pro.domain.withdraw.repository.WithdrawApplyRepository;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceImplTest {

    @InjectMocks
    protected WithdrawServiceImpl withdrawService;
    @Mock
    protected WithdrawApplyRepository withdrawApplyRepository;
    @Mock
    protected StoreService storeService;
    @Mock
    protected MemberService memberService;
    @Mock
    protected CashService cashService;

    @DisplayName("출금 요청 금액이 잔여 캐시보다 많으면 검증에 실패한다.")
    @Test
    void apply_fail() throws Exception {
        //given
        WithDrawApplyForm withDrawApplyForm = new WithDrawApplyForm("국민은행", "123-456-789", 10000);

        int storeRestCash = 5000;
        Member member = mock(Member.class);
        given(member.getBrand()).willReturn("나이키");
        given(storeService.getRestCash(eq("나이키"))).willReturn(storeRestCash);

        //when
        RsData<WithdrawApply> result = withdrawService.apply(withDrawApplyForm, member);

        //then
        Assertions.assertThat(result.isFail()).isTrue();
        Assertions.assertThat(result.getMsg()).isEqualTo("출금 요청 금액은 잔여 캐시보다 많을 수 없습니다.");
    }

    @DisplayName("출금 요청 금액이 잔여 캐시보다 적으면 출금 요청에 성공한다.")
    @Test
    void apply_success() throws Exception {
        //given
        WithDrawApplyForm withDrawApplyForm = new WithDrawApplyForm("국민은행", "123-456-789", 10000);

        int storeRestCash = 15000;
        Member member = mock(Member.class);
        given(member.getBrand()).willReturn("나이키");
        given(storeService.getRestCash(eq("나이키"))).willReturn(storeRestCash);

        //when
        RsData<WithdrawApply> result = withdrawService.apply(withDrawApplyForm, member);

        //then
        Assertions.assertThat(result.isSuccess()).isTrue();
        Assertions.assertThat(result.getMsg()).isEqualTo("출금 신청이 완료되었습니다.");
    }

    @DisplayName("이미 처리된 출금 신청은 기각된다.")
    @Test
    void withdraw_fail() throws Exception {
        //given
        WithDrawApplyForm withDrawApplyForm = new WithDrawApplyForm("국민은행", "123-456-789", 10000);
        Member member = mock(Member.class);
        WithdrawApply withdrawApply = WithdrawApply.of(withDrawApplyForm, member);
        withdrawApply.setApplyDone(mock(CashLog.class), "출금 완료");
        given(withdrawApplyRepository.findById(anyLong())).willReturn(Optional.of(withdrawApply));

        //when
        RsData result = withdrawService.withdraw(1L);

        //then
        Assertions.assertThat(result.isFail()).isTrue();
        Assertions.assertThat(result.getMsg()).isEqualTo("이미 처리되었습니다.");
    }

    @DisplayName("취소되지 않았고, 출금되지 않았고, 잔여캐시가 출금신청 캐시보다 많다면 출금에 성공한다.")
    @Test
    void withdraw_success() throws Exception {
        //given
        WithDrawApplyForm withDrawApplyForm = new WithDrawApplyForm("국민은행", "123-456-789", 10000);
        Member member = createMember();
        WithdrawApply withdrawApply = WithdrawApply.of(withDrawApplyForm, member);
        given(withdrawApplyRepository.findById(anyLong())).willReturn(Optional.of(withdrawApply));
        given(cashService.addCashLog(withdrawApply)).willReturn(CashLog.of(withdrawApply));
        given(storeService.getRestCash(eq("Polo"))).willReturn(20000);

        //when
        RsData result = withdrawService.withdraw(1L);

        //then
        Assertions.assertThat(result.isSuccess()).isTrue();
        Assertions.assertThat(result.getMsg()).isEqualTo("출금신청이 처리되었습니다. %s원이 출금되었습니다.".formatted(String.format("%,d",
                withdrawApply.getPrice())));
    }



    private Member createMember() {
        Member member = Member.builder()
                .email("brand@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.BRAND_ADMIN)
                .brand("Polo")
                .build();
        return member;
    }

}