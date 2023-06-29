package project.trendpick_pro.domain.ask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
import project.trendpick_pro.domain.answer.service.AnswerService;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.service.AskService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.global.rsData.RsData;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/customerservice/asks")
public class AskController {
    private final AskService askService;
    private final AnswerService answerService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/register")
    public String registerForm(@RequestParam("product") Long productId, AskForm askForm, Model model) {
        askForm.setProductId(productId);
        model.addAttribute("askForm", askForm);
        return "trendpick/customerservice/asks/register";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public String registerAsk(@Valid AskForm askForm) {
        RsData<Long> result = askService.register(rq.getMember(), askForm);
        if(result.isFail())
            return rq.redirectWithMsg("/trendpick/products/%s".formatted(askForm.getProductId()), result);

        return rq.redirectWithMsg("/trendpick/products/%s".formatted(askForm.getProductId()), result);
    }

    @GetMapping("/{askId}")
    public String showAsk(@PathVariable Long askId, AnswerForm answerForm, Model model) {
        model.addAttribute("ask", askService.show(askId));
        model.addAttribute("answers", answerService.showAll(askId));
        model.addAttribute("answerForm", new AnswerForm());
        return "trendpick/customerservice/asks/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{askId}")
    public String deleteAsk(@PathVariable Long askId) {
        RsData<Long> result = askService.delete(rq.getMember(), askId);
        if(result.isFail())
            return rq.historyBack(result);

        return rq.redirectWithMsg("/trendpick/products/%s".formatted(result.getData()), result);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{askId}")
    public String modifyForm(@PathVariable Long askId, Model model) {
        AskResponse ask = askService.show(askId);
        if(!Objects.equals(ask.getMemberId(), rq.getMember().getId()))
            return rq.historyBack("자신이 올린 문의글에 대해서만 수정 권한이 있습니다.");

        model.addAttribute("askForm", new AskForm(ask.getAskId(), ask.getTitle(), ask.getContent()));

        return "trendpick/customerservice/asks/register";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/edit/{askId}")
    public String modifyAsk(@PathVariable Long askId, @Valid AskForm askForm) {
        RsData<AskResponse> result = askService.modify(rq.getMember(), askId, askForm);
        if (result.isFail())
            return rq.redirectWithMsg("/trendpick/customerservice/asks/%s".formatted(askId), result);

        return rq.redirectWithMsg("/trendpick/customerservice/asks/%s".formatted(askId), "문의글 수정이 완료되었습니다.");
    }

}