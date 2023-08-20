package project.trendpick_pro.domain.member.service;

import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.response.MemberInfoResponse;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.tags.tag.entity.dto.request.TagRequest;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.global.util.rsData.RsData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    RsData<Long> join(JoinForm joinForm);
    RsData<List<Member>> joinAll(List<JoinForm> joinForms);
    void modifyTag(Member member, TagRequest tagRequest);
    RsData<MemberInfoResponse> modifyAddress(Member member, String address);
    Member findById(Long id);
    Member findBrandMember(String name);
    Optional<Member> findByEmail(String username);
    void updateRecentlyAccessDate(Member member, LocalDateTime dateTime);
    long getRestCash(Member member);
    void completeWithdraw(WithdrawApply withdrawApply);
}