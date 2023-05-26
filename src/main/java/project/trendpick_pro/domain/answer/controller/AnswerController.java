package project.trendpick_pro.domain.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.answer.entity.dto.AnswerRequestDto;
import project.trendpick_pro.domain.ask.entity.Ask;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {

    @GetMapping("/list")
    public String showAnswers(){

        return "redirect:/ask/list";
    }

    @PostMapping("/{id}")
    public String addAnswer(@PathVariable Long askId,
                            @Valid AnswerRequestDto answerRequestDto){

        return "redirect:/ask/list";
    }

    @DeleteMapping("/{id}")
    public String deleteAnswer(@PathVariable Long id){

        return "redirect:/ask/list";
    }

    @PatchMapping("/{id}")
    public String modifyAnswer(@PathVariable Long id){

        return "redirect:/ask/list";
    }


}
