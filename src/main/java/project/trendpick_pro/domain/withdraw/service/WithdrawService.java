package project.trendpick_pro.domain.withdraw.service;

import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

public interface WithdrawService {
    RsData<WithdrawApply> apply(String bankName, String bankAccountNo, Integer price, Member applicant);
    List<WithdrawApply> findAll();
    List<WithdrawApply> findByWithdrawApplyId(Long id);
    RsData withdraw(Long withdrawApplyId);
    RsData cancelApply(Long withdrawApplyId);
}
