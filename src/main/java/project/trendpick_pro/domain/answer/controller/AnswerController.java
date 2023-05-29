package project.trendpick_pro.domain.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.answer.entity.dto.request.AnswerRequest;
import project.trendpick_pro.domain.answer.service.AnswerService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/customerservice/asks/{askId}/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final Rq rq;

    @PostMapping("/register")
    public String registerAnswer(@PathVariable Long askId,
                            @Valid AnswerRequest answerRequest){
        Member member = rq.getMember();
        answerService.register(askId, member, answerRequest);

        return "redirect:/trendpick/customerservice/asks/{askId}".formatted(askId);
    }

    @PostMapping("/delete/{answerId}")
    public String deleteAnswer(@PathVariable Long askId, @PathVariable Long answerId){
        answerService.delete(answerId);

        return "redirect:/trendpick/customerservice/asks/%s".formatted(askId);
    }

    @PostMapping("/moidfy/{answerId}")
    public String modifyAnswer(@PathVariable Long askId, @PathVariable Long answerId, @Valid AnswerRequest answerRequest){
        answerService.modify(answerId, answerRequest);

        return "redirect:/trendpick/customerservice/asks/%s".formatted(askId);
    }
}
