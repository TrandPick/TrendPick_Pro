package project.trendpick_pro.domain.withdraw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.domain.withdraw.repository.WithdrawApplyRepository;
import project.trendpick_pro.global.rsData.RsData;
import project.trendpick_pro.global.util.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WithdrawService {
    private final WithdrawApplyRepository withdrawApplyRepository;
    private final MemberService memberService;
    private final BrandService brandService;
    public RsData<WithdrawApply> apply(String bankName, String bankAccountNo, Integer price, Member applicant) {
        WithdrawApply withdrawApply = WithdrawApply.builder()
                .bankName(bankName)
                .bankAccountNo(bankAccountNo)
                .price(price)
                .applicant(applicant)
                .build();

        withdrawApplyRepository.save(withdrawApply);

        return RsData.of("S-1", "출금 신청이 완료되었습니다.", withdrawApply);
    }

    public List<WithdrawApply> findAll() {
        return withdrawApplyRepository.findAll();
    }

    public List<WithdrawApply> findByWithdrawApplyId(Long id){
        List<WithdrawApply> findByWithdrawApplyList=withdrawApplyRepository.findAll();
        List<WithdrawApply> withdrawApplyList=new ArrayList<>();

        for(WithdrawApply withdrawApply: findByWithdrawApplyList){
            if(Objects.equals(withdrawApply.getApplicant().getId(),id)){
                withdrawApplyList.add(withdrawApply);
            }
        }
        return withdrawApplyList;
    }

    public RsData withdraw(Long withdrawApplyId) {
        WithdrawApply withdrawApply = withdrawApplyRepository.findById(withdrawApplyId).orElse(null);

        if (withdrawApply == null) {
            return RsData.of("F-1", "출금신청 데이터를 찾을 수 없습니다.");
        }

        if (withdrawApply.isApplyDoneAvailable() == false) {
            return RsData.of("F-2", "이미 처리되었습니다.");
        }

        long restCash = memberService.getRestCash(withdrawApply.getApplicant());

        if (withdrawApply.getPrice() > restCash) {
            return RsData.of("F-3", "예치금이 부족합니다.");
        }
        Brand brand=brandService.findByName(withdrawApply.getApplicant().getBrand());

        CashLog cashLog = memberService.addCash(
                        withdrawApply.getApplicant().getBrand(),
                        withdrawApply.getPrice() * -1,
                        brand,
                        CashLog.EvenType.출금__통장입금
                )
                .getData().getCashLog();

        withdrawApply.setApplyDone(cashLog.getId(), "관리자에 의해서 처리되었습니다.");

        return RsData.of(
                "S-1",
                "%d번 출금신청이 처리되었습니다. %s원이 출금되었습니다.".formatted(withdrawApply.getId(), Ut.nf(withdrawApply.getPrice())),
                Ut.mapOf(
                        "cashLogId", cashLog.getId()
                )
        );
    }

    public RsData cancelApply(Long withdrawApplyId) {
        WithdrawApply withdrawApply = withdrawApplyRepository.findById(withdrawApplyId).orElse(null);

        if (withdrawApply == null) {
            return RsData.of("F-1", "출금신청 데이터를 찾을 수 없습니다.");
        }

        if (withdrawApply.isCancelAvailable() == false) {
            return RsData.of("F-2", "취소가 불가능합니다.");
        }

        withdrawApply.setCancelDone("관리자에 의해서 취소되었습니다.");

        return RsData.of(
                "S-1",
                "%d번 출금신청이 취소되었습니다.".formatted(withdrawApply.getId()),
                null
        );
    }
}