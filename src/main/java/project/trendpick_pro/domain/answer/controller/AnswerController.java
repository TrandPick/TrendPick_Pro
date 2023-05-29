package project.trendpick_pro.domain.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.answer.entity.dto.request.AnswerRequest;
import project.trendpick_pro.domain.answer.entity.dto.response.AnswerResponse;
import project.trendpick_pro.domain.answer.service.AnswerService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/customerservice/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public String registerAnswer(@RequestParam("ask") Long askId, @Valid AnswerRequest answerRequest){
        Member member = rq.getMember();
        answerService.register(askId, member, answerRequest);

        return "redirect:/trendpick/customerservice/asks/{askId}".formatted(askId);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{answerId}")
    public String deleteAnswer(@PathVariable Long answerId){
        AnswerResponse res = answerService.delete(rq.getMember(), answerId);

        return "redirect:/trendpick/customerservice/asks/%s".formatted(res.getAsk().getId());
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/moidfy/{answerId}")
    public String modifyAnswer(@PathVariable Long answerId, @Valid AnswerRequest answerRequest){
        AnswerResponse res = answerService.modify(rq.getMember(), answerId, answerRequest);

        return "redirect:/trendpick/customerservice/asks/%s".formatted(res.getAsk().getId());
    }
}
