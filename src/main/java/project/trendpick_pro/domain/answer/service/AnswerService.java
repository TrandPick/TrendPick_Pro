package project.trendpick_pro.domain.answer.service;

import project.trendpick_pro.domain.answer.entity.dto.response.AnswerResponse;
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.global.rsData.RsData;

import java.util.List;

public interface AnswerService {
    RsData<Long> register(Member member, Long askId, AnswerForm answerForm);
    RsData<Long> delete(Member member, Long answerId);
    RsData<Long> modify(Member member, Long answerId, AnswerForm answerForm);
    List<AnswerResponse> showAll(Long askId);
}
