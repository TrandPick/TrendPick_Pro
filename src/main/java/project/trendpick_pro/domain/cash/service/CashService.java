package project.trendpick_pro.domain.cash.service;


import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.member.entity.Member;


public interface CashService {
    CashLog addCash(Member member, long price, String relTypeCode,Long relId, CashLog.EvenType eventType);

}