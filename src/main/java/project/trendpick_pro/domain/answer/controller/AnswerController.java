package project.trendpick_pro.domain.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
import project.trendpick_pro.domain.answer.service.AnswerService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("register")
    public String register(@RequestParam("ask") Long askId, @Valid AnswerForm answerForm){
        RsData<Long> result = answerService.register(rq.getAdmin(), askId, answerForm);
        if(result.isFail()) {
            return rq.historyBack(result);
        } return rq.redirectWithMsg("/trendpick/asks/%s".formatted(askId), result);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{answerId}")
    public String deleteAnswer(@PathVariable Long answerId){
        RsData<Long> result = answerService.delete(rq.getAdmin(), answerId);
        if(result.isFail()) {
            return rq.historyBack(result);
        } return rq.redirectWithMsg("/trendpick/asks/%s".formatted(result.getData()), result);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/moidfy/{answerId}")
    public String modifyAnswer(@PathVariable Long answerId, @Valid AnswerForm answerForm){
        RsData<Long> result = answerService.modify(rq.getAdmin(), answerId, answerForm);
        if(result.isFail()) {
            return rq.historyBack(result);
        } return rq.redirectWithMsg("/trendpick/asks/%s".formatted(result.getData()), result);
    }
}