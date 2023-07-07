package project.trendpick_pro.domain.cash.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.repository.CashLogRepository;
import project.trendpick_pro.domain.member.entity.Member;

@Service
@RequiredArgsConstructor
public class CashService {
    private final CashLogRepository cashLogRepository;

    public CashLog addCash(Member member, long price, String relTypeCode,Long relId, CashLog.EvenType eventType) {
        CashLog cashLog = CashLog.builder()
                .member(member)
                .price(price)
                .relTypeCode(relTypeCode)
                .relId(relId)
                .eventType(eventType)
                .build();

        cashLogRepository.save(cashLog);

        return cashLog;
    }
}