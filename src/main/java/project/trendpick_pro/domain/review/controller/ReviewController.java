package project.trendpick_pro.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.service.ReviewService;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final Rq rq;

    @PostMapping("/write")
    public String createReview(@Valid ReviewSaveRequest reviewCreateRequest, @RequestParam(value = "product") Long productId,
                               Model model) throws Exception {
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
    public String updateReview(@PathVariable Long reviewId, ReviewSaveRequest reviewSaveRequest,
                               Model model) throws IOException {
        ReviewResponse reviewResponse = reviewService.update(reviewId, reviewSaveRequest);

        model.addAttribute("reviewResponse", reviewResponse);
        return "redirect:/trendpick/review";
    }
}
