package project.trendpick_pro.domain.ask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.answer.entity.dto.AnswerRequestDto;
import project.trendpick_pro.domain.ask.entity.dto.AskRequestDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ask")
public class AskController {

    @GetMapping("/list")
    public String showAsks(){

        return "/ask/list";
    }

    @GetMapping("/detail/{id}")
    public String showAsk(@PathVariable Long id){

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
    public String addAsk(@Valid AskRequestDto askRequestDto, @PathVariable Long brandId){

        return "redirect:/ask/list";
    }
}
