package project.trendpick_pro.domain.ask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.service.AskService;
import project.trendpick_pro.domain.common.base.rq.Rq;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/customerservice/asks")
public class AskController {
    private final AskService askService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/register")
    public String registerForm(@ModelAttribute AskForm askForm, Model model) {
        askForm.setProductId(1L);
        model.addAttribute("askForm", askForm);
        return "trendpick/customerservice/asks/register";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public String registerAsk(@Valid AskForm askForm) {
        AskResponse askResponse = askService.register(rq.CheckMember().get(), askForm);
        return "redirect:/trendpick/customerservice/asks/%s".formatted(askResponse.getAskId());
    }

    @GetMapping("/{askId}")
    public String showAsk(@PathVariable Long askId, AnswerForm answerForm, Model model) {
        model.addAttribute("askResponse", askService.show(askId));
        return "trendpick/customerservice/asks/detail";
    }

    @GetMapping("/list")
    public String showAsksByProduct(@RequestParam("page") int offset, @RequestParam("product") Long productId, Model model) {
        model.addAttribute("askResponse", askService.showAsksByProduct(productId, offset));
        return "trendpick/customerservice/asks/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{askId}")
    public String deleteAsk(@PathVariable Long askId) {
        askService.delete(rq.getMember(), askId);

        return "redirect:/trendpick/customerservice/asks/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/edit/{askId}")
    public String modifyAsk(@PathVariable Long askId, @Valid AskRequest askRequest, Model model) {
        AskResponse askResponse = askService.modify(rq.getMember(), askId, askRequest);

        model.addAttribute("askResponse", askResponse);
        return "redirect:/trendpick/customerservice/asks/%s".formatted(askId);
    }

}
