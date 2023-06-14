package project.trendpick_pro.domain.recommend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import project.trendpick_pro.domain.recommend.service.RecommendService;

@Controller
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/admin/caculate")
    public String caculate(){
        recommendService.select();
        return "redirect:/trendpick/products/list";
    }
}
