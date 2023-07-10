package project.trendpick_pro.domain.member.service;

import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.entity.dto.CashResponse;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.tags.tag.entity.dto.request.TagRequest;
import project.trendpick_pro.global.rsData.RsData;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    RsData<Member> register(JoinForm joinForm);
    RsData<List<Member>> saveAll(List<JoinForm> joinForms);
    void manageTag(String username, TagRequest tagRequest);
    RsData<Member> manageAddress(Member member, String address);
    Member findById(Long id);
    Member findByBrandMember(String name);
    Optional<Member> findByEmail(String username);
    void updateRecentlyAccessDate(Member member);
    RsData<CashResponse> addCash(String brand, long price, Brand relEntity, CashLog.EvenType eventType);
    long getRestCash(Member member);
}