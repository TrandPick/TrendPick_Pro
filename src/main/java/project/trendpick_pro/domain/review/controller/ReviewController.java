package project.trendpick_pro.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.service.ReviewService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final MemberRepository memberRepository;
    private final Rq rq;

    @GetMapping("/register")
    public String registerReview() {
        return "/trendpick/review/register";
    }

    @PostMapping("/register")
    public String createReview(@Valid ReviewSaveRequest reviewCreateRequest,
                               @RequestParam("mainFile") MultipartFile mainFile,
                               @RequestParam("subFiles") List<MultipartFile> subFiles, Model model) throws Exception {
        Member member = rq.getMember();

        ReviewResponse reviewResponse = reviewService.createReview(member, 1L, reviewCreateRequest, mainFile, subFiles);

        model.addAttribute("reviewResponse", reviewResponse);
        return "redirect:/trendpick/review/list";

    }

    @GetMapping("/{reviewId}")
    public String showReview(@PathVariable Long reviewId, Model model){
        ReviewResponse reviewResponse = reviewService.showReview(reviewId);
        model.addAttribute("reviewResponse", reviewResponse);
        return "/trendpick/review/detail";
    }

    @GetMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);

        return "redirect:/trendpick/review/list";
    }

    @GetMapping("/edit/{reviewId}")
    public String showUpdateReview(@PathVariable Long reviewId, Model model){
        Review review = reviewService.findById(reviewId);
        model.addAttribute("originReview", review);

        return "trendpick/review/modify";
    }

    @PostMapping("/edit/{reviewId}")
    public String updateReview(@PathVariable Long reviewId, ReviewSaveRequest reviewSaveRequest, @RequestParam("mainFile") MultipartFile mainFile,
                               @RequestParam("subFiles") List<MultipartFile> subFiles, Model model) throws IOException {
        ReviewResponse reviewResponse = reviewService.modify(reviewId, reviewSaveRequest, mainFile, subFiles);

        model.addAttribute("reviewResponse", reviewResponse);
        return "redirect:/trendpick/review/" + reviewId;
    }


    @GetMapping("/list")
    public String showAllReview(Pageable pageable, Model model){
        Page<ReviewResponse> reviewResponses = reviewService.showAll(pageable);
        model.addAttribute("reviewResponses", reviewResponses);
        return "/trendpick/review/list";
    }
}
