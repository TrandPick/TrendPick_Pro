package project.trendpick_pro.domain.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.answer.entity.dto.request.AnswerRequest;
import project.trendpick_pro.domain.answer.entity.dto.response.AnswerResponse;
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
import project.trendpick_pro.domain.answer.repository.AnswerRepository;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.repository.AskRepository;
import project.trendpick_pro.domain.ask.service.AskService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final AskRepository askRepository;

    @Transactional
    public void register(Long askId, AnswerForm answerForm) {
        Ask ask = askRepository.findById(askId).orElseThrow();
        Answer answer = Answer.write(ask, answerForm);
        answerRepository.save(answer);
    }

    @Transactional
    public AnswerResponse delete(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow();
        answerRepository.delete(answer);
        answer.getAsk().getAnswerList().remove(answer);

        return AnswerResponse.of(answer);
    }

    public AnswerResponse modify(Long answerId, AnswerForm answerForm) {
        Answer answer = answerRepository.findById(answerId).orElseThrow();
        answer.update(answerForm);
        return AnswerResponse.of(answer);
    }
}
