package project.trendpick_pro.domain.withdraw.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.service.CashService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.domain.withdraw.entity.dto.WithDrawApplyForm;
import project.trendpick_pro.domain.withdraw.repository.WithdrawApplyRepository;
import project.trendpick_pro.domain.withdraw.service.WithdrawService;
import project.trendpick_pro.global.util.rsData.RsData;
import project.trendpick_pro.global.util.Ut;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithdrawServiceImpl implements WithdrawService {

    private final WithdrawApplyRepository withdrawApplyRepository;
    private final StoreService storeService;
    private final MemberService memberService;
    private final CashService cashService;

    @Transactional
    @Override
    public RsData<WithdrawApply> apply(WithDrawApplyForm withDrawApplyForm, Member applicant) {
        RsData validateResult = validateAvailableApply(withDrawApplyForm, applicant);
        if(validateResult.isFail())
            return validateResult;
        WithdrawApply withdrawApply = withdrawApplyRepository.save(WithdrawApply.of(withDrawApplyForm, applicant));
        return RsData.of("S-1", "출금 신청이 완료되었습니다.", withdrawApply);
    }

    @Override
    public List<WithdrawApply> findAll() {
        return withdrawApplyRepository.findAll();
    }

    @Override
    public List<WithdrawApply> findAllWithdrawByStoreName(String storeName){
        return withdrawApplyRepository.findWithdrawsByStoreName(storeName);
    }

    @Transactional
    public RsData withdraw(Long withdrawApplyId) {
        WithdrawApply withdrawApply = withdrawApplyRepository.findById(withdrawApplyId).orElse(null);

        RsData validateResult = validateAvailableWithdraw(withdrawApply); //캐시 출금 검증
        if (validateResult.isFail()) return validateResult;

        CashLog cashLog = cashService.addCashLog(withdrawApply);
        withdrawApply.setApplyDone(cashLog, "관리자에 의해서 처리되었습니다.");
        memberService.completeWithdraw(withdrawApply); //출금 신청한거 완료됐어요.

        return RsData.of(
                "S-1",
                "출금신청이 처리되었습니다. %s원이 출금되었습니다.".formatted(Ut.nf(withdrawApply.getPrice()))
        );
    }


    @Transactional
    public RsData cancelApply(Long withdrawApplyId) {
        WithdrawApply withdrawApply = withdrawApplyRepository.findById(withdrawApplyId).orElse(null);

        RsData validateResult = validateAvailableCancel(withdrawApply); //취소 가능한지 검증
        if (validateResult.isFail()) return validateResult;

        withdrawApply.setCancelDone("관리자에 의해서 취소되었습니다.");

        return RsData.of(
                "S-1",
                "%d번 출금신청이 취소되었습니다.".formatted(withdrawApply.getId()),
                null
        );
    }

    private  RsData validateAvailableCancel(WithdrawApply withdrawApply) {
        if (withdrawApply == null)
            return RsData.of("F-1", "출금신청 데이터를 찾을 수 없습니다.");

        if (!withdrawApply.checkAlreadyProcessed())
            return RsData.of("F-2", "이미 처리된 출금 신청 입니다.");

        return RsData.success();
    }
    private RsData validateAvailableWithdraw(WithdrawApply withdrawApply) {
        if (withdrawApply == null)
            return RsData.of("F-1", "출금신청 데이터를 찾을 수 없습니다.");
        if (withdrawApply.checkAlreadyProcessed())
            return RsData.of("F-2", "이미 처리되었습니다.");
        if (withdrawApply.getPrice() >  storeService.getRestCash(withdrawApply.getApplicant().getBrand()))
            return RsData.of("F-3", "캐시보다 더 많은 금액을 출금할 수 없습니다.");
        return RsData.success();
    }

    private RsData validateAvailableApply(WithDrawApplyForm withDrawApplyForm, Member applicant) {
        if(withDrawApplyForm.getPrice() > storeService.getRestCash(applicant.getBrand()))
            return RsData.of("F-1", "출금 요청 금액은 잔여 캐시보다 많을 수 없습니다.");
        return RsData.success();
    }

    @Override
    public int showRestCash(Member brandMember) {
        return storeService.getRestCash(brandMember.getBrand());
    }
}