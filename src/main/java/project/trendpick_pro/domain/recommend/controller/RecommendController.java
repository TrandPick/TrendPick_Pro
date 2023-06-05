package project.trendpick_pro.domain.recommend.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.recommend.entity.Recommend;
import project.trendpick_pro.domain.recommend.service.RecommendService;

@Controller
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;

    @GetMapping("/admin/caculate")
    public String caculate(){
//        recommendService.select();
        return "redirect:/trendpick/products/list";
    }

    @GetMapping("/admin/getrecommendset")
    public String getRecommend(Model model, @RequestParam("page") int offset){
        model.addAttribute("recommend", recommendService.getFindAll(offset));
        return "/main";
    }
}
