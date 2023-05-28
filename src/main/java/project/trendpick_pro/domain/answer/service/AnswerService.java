package project.trendpick_pro.domain.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.answer.entity.dto.request.AnswerRequest;
import project.trendpick_pro.domain.answer.repository.AnswerRepository;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.repository.AskRepository;
import project.trendpick_pro.domain.ask.service.AskService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final AskRepository askRepository;

    @Transactional
    public void register(Long askId, String member, AnswerRequest answerRequest) {
        Ask ask = askRepository.findById(askId).orElseThrow();

        Answer answer = Answer.write(ask, member, answerRequest);

        answerRepository.save(answer);
    }

    @Transactional
    public void delete(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow();

        answerRepository.delete(answer);
        answer.getAsk().getAnswerList().remove(answer);
    }

    public void modify(Long answerId, AnswerRequest answerRequest) {
        Answer answer = answerRepository.findById(answerId).orElseThrow();

        answer.update(answerRequest);
    }
}
