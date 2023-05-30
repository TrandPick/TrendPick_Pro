package project.trendpick_pro.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewCreateRequest;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewUpdateRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.service.ReviewService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final Rq rq;

    @PostMapping("/write")
    public String createReview(@Valid ReviewCreateRequest reviewCreateRequest, @RequestParam(value = "product") Long productId, Model model) {
        Member actor = rq.getMember();
        ReviewResponse reviewResponse = reviewService.createReview(actor, productId, reviewCreateRequest);

        model.addAttribute("reviewResponse", reviewResponse);
        return "redirect:/trendpick/review";

    }

    @GetMapping("/list/{reviewId}")
    public String showReview(@PathVariable Long reviewId, Model model){
        ReviewResponse reviewResponse = reviewService.showReview(reviewId);
        model.addAttribute("reviewResponse", reviewResponse);
        return "redirect:/trendpick/review";
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);

        return "redirect:/trendpick/review";
    }

    @PostMapping("/edit/{reviewId}")
    public String modifyReview(@PathVariable Long reviewId, ReviewUpdateRequest reviewUpdateRequest, Model model) {
        ReviewResponse reviewResponse = reviewService.modify(reviewId, reviewUpdateRequest);

        model.addAttribute("reviewResponse", reviewResponse);
        return "redirect:/trendpick/review";
    }
}
