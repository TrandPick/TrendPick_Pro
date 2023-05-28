package project.trendpick_pro.domain.ask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.service.AskService;

@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/customerservice/asks")
public class AskController {
    private final AskService askService;

    @GetMapping("/list")
    public String showAllAsk(Model model) {
        model.addAttribute("askResponse", askService.showAll());
        return "trendpick/customerservice/ask/list";
    }

    @GetMapping("/{askId}}")
    public String showAsk(@PathVariable Long askId, Model model) {
        model.addAttribute("askResponse", askService.show(askId));
        return "trendpick/customerservice/ask/detail";
    }

    @PostMapping("/delete/{askId}")
    public String deleteAsk(@PathVariable Long askId) {
        askService.delete(askId);

        return "redirect:/trendpick/asks/list";
    }

    @PostMapping("/edit/{askId}")
    public String modifyAsk(@PathVariable Long askId, @Valid AskRequest askRequest, Model model) {
        AskResponse askResponse = askService.modify(askId, askRequest);

        model.addAttribute("askResponse", askResponse);
        return "redirect:/trendpick/asks/%s".formatted(askId);
    }

    @PostMapping("/register")
    public String registerAsk(@Valid AskRequest askRequest, @RequestParam(value = "brand") Long brandId, Model model) {
        AskResponse askResponse = askService.register(askRequest);

        model.addAttribute("askResponse", askResponse);
        return "redirect:/trendpick/asks/%s".formatted(askResponse.getId());
    }
}
