package project.trendpick_pro.domain.cash.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.repository.CashLogRepository;
import project.trendpick_pro.domain.cash.service.CashService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashServiceImpl implements CashService {
    private final CashLogRepository cashLogRepository;
    @Transactional
    @Override
    public CashLog addCashLog(WithdrawApply withdrawApply) {
        CashLog cashLog = cashLogRepository.save(CashLog.of(withdrawApply));
        return cashLog;
    }

    @Transactional
    @Override
    public CashLog addCashLog(RebateOrderItem rebateOrderItem) {
        CashLog cashLog = cashLogRepository.save(CashLog.of(rebateOrderItem));
        return cashLog;
    }


}
