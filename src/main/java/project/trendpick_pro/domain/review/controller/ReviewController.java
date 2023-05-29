package project.trendpick_pro.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.review.service.ReviewService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/review")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/list/{productId}")
    public String showReview(@PathVariable Long reviewId, Model model){
        model.addAttribute("reviewRes", reviewService.showReview(reviewId));
        return "";
    }
}
