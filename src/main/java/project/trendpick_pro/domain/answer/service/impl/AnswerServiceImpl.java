package project.trendpick_pro.domain.answer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.answer.entity.dto.response.AnswerResponse;
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
import project.trendpick_pro.domain.answer.repository.AnswerRepository;
import project.trendpick_pro.domain.answer.service.AnswerService;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.repository.AskRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final AskRepository askRepository;

    @Transactional
    public RsData<Long> register(Member member, Long askId, AnswerForm answerForm) {
        Ask ask = askRepository.findById(askId).orElseThrow(
                () -> new NoSuchElementException("해당 문의는 없는 문의입니다.")
        );
        if (!member.getBrand().equals(ask.getProduct().getProductOption().getBrand().getName()))
            return RsData.of("F-1", "타 브랜드 상품에 대한 문의글에는 답변 권한이 없습니다.");

        Answer answer = Answer.write(answerForm.getContent());
        answer.connectAsk(ask);
        answerRepository.save(answer);
        return RsData.of("S-1", "답변이 성공적으로 등록되었습니다.", ask.getId());
    }

    @Transactional
    public RsData<Long> delete(Member member, Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new NoSuchElementException("해당 답변은 없는 답변입니다.")
        );

        if(!answer.getAsk().getProduct().getProductOption().getBrand().getName().equals(member.getBrand()))
            return RsData.of("F-1", "접근 권한이 없습니다.");

        Ask ask = answer.getAsk();
        ask.getAnswerList().remove(answer);
        if(ask.getAnswerList().size() == 0)
            ask.updateStatusYet();

        return RsData.of("S-1", "답변이 삭제되었습니다.", answer.getAsk().getId());
    }

    @Transactional
    public RsData<Long> modify(Member member, Long answerId, AnswerForm answerForm) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new NoSuchElementException("해당 답변은 없는 답변입니다.")
        );

        if(!Objects.equals(answer.getAsk().getAuthor().getBrand(), member.getBrand()))
            return RsData.of("F-1", "접근 권한이 없습니다.");
        answer.update(answerForm);

        return RsData.of("S-1", "답변이 수정되었습니다.", answer.getAsk().getId());
    }

    @Transactional
    public List<AnswerResponse> showAll(Long askId) {
        List<Answer> answers = answerRepository.findAllByAskId(askRepository.findById(askId).get());
        return AnswerResponse.of(answers);
    }
}

