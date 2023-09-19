package project.trendpick_pro.domain.withdraw.service;

import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.domain.withdraw.entity.dto.WithDrawApplyForm;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

public interface WithdrawService {
    RsData<WithdrawApply> apply(WithDrawApplyForm withDrawApplyForm, Member applicant);
    List<WithdrawApply> findAll();
    List<WithdrawApply> findAllWithdrawByStoreName(String storeName);
    RsData withdraw(Long withdrawApplyId);
    RsData cancelApply(Long withdrawApplyId);
    int showRestCash(Member brandMember);
}
