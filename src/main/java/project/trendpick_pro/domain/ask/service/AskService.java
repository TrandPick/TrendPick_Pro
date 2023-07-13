package project.trendpick_pro.domain.ask.service;

import org.springframework.data.domain.Page;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.global.util.rsData.RsData;

public interface AskService {
    RsData<Long> register(Member member, AskForm askForm);
    RsData<AskResponse> modify(Member member, Long askId, AskForm askForm);
    RsData<Long> delete(Member member, Long askId);
    AskResponse show(Long askId);
    Page<AskResponse> showAsksByProduct(Long productId, int offset);
}