package project.trendpick_pro.domain.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.answer.entity.dto.request.AnswerRequest;
import project.trendpick_pro.domain.answer.entity.dto.response.AnswerResponse;
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
import project.trendpick_pro.domain.answer.service.AnswerService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/customerservice/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("register/{askId}")
    public String register(@PathVariable Long askId, @Valid AnswerForm answerForm){
        rq.CheckAdmin().get();
        answerService.register(askId, answerForm);

        return "redirect:/trendpick/customerservice/asks/{askId}".formatted(askId);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{answerId}")
    public String deleteAnswer(@PathVariable Long answerId){
        rq.CheckAdmin().get();
        AnswerResponse answerResponse = answerService.delete(answerId);

        return "redirect:/trendpick/customerservice/asks/%s".formatted(answerResponse.getAskId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/moidfy/{answerId}")
    public String modifyAnswer(@PathVariable Long answerId, @Valid AnswerForm answerForm){
        rq.CheckAdmin().get();
        AnswerResponse answerResponse = answerService.modify(answerId, answerForm);
        return "redirect:/trendpick/customerservice/asks/%s".formatted(answerResponse.getAskId());
    }
}
