package project.trendpick_pro.domain.cash.service;


import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;


public interface CashService {
    CashLog addCashLog(WithdrawApply withdrawApply);

    CashLog addCashLog(RebateOrderItem rebateOrderItem);
}