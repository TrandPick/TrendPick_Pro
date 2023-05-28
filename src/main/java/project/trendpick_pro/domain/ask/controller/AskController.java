package project.trendpick_pro.domain.ask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ask")
public class AskController {

    @GetMapping("/list")
    public String showAllAsk(){

        return "/ask/list";
    }

    @GetMapping("/{askId}}")
    public String showAsk(@PathVariable Long askId){

        return "/ask/detail";
    }

    @PostMapping("/delete/{id}")
    public String deleteAsk(@PathVariable Long id){

        return "redirect:/ask/get/%s".formatted(id);
    }

    @PostMapping("/modify/{id}")
    public String modifyAsk(@PathVariable Long id){

        return "redirect:/ask/get/%s".formatted(id);
    }

    @PostMapping("/add/{brandId}")
    public String addAsk(@Valid AskRequest askRequestDto, @PathVariable Long brandId){

        return "redirect:/ask/list";
    }
}
