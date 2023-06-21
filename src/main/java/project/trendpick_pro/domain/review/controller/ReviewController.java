package project.trendpick_pro.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.service.ReviewService;
import project.trendpick_pro.global.rsData.RsData;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final Rq rq;

    @GetMapping("/register/{productId}")
    public String registerReview(@PathVariable("productId") Long productId, Model model) {
        model.addAttribute("productId", productId);
        return "trendpick/review/register";
    }

    @PostMapping("/register/{productId}")
    public String createReview(@Valid ReviewSaveRequest reviewCreateRequest,
                               @RequestParam("mainFile") MultipartFile mainFile,
                               @RequestParam("subFiles") List<MultipartFile> subFiles,
                               @PathVariable("productId") Long productId, Model model) throws Exception {
        Member checkMember = rq.getLogin();
        RsData<ReviewResponse> reviewResponse = reviewService.createReview(checkMember, productId, reviewCreateRequest, mainFile, subFiles);

        model.addAttribute("reviewResponse", reviewResponse);
        return rq.redirectWithMsg("/trendpick/review/list", reviewResponse);
    }

    @GetMapping("/{reviewId}")
    public String showReview(@PathVariable Long reviewId, Model model){
        ReviewResponse reviewResponse = reviewService.showReview(reviewId);
        model.addAttribute("reviewResponse", reviewResponse);
        if (rq.checkLogin() && !rq.checkAdmin()) {
            Member checkMember = rq.getMember();
            String writer = checkMember.getUsername();
            model.addAttribute("currentUser", writer);
        }
        return "trendpick/review/detail";
    }

    @DeleteMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);

        return rq.redirectWithMsg("/trendpick/review/list", "삭제가 완료되었습니다.");
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
        RsData<ReviewResponse> reviewResponse = reviewService.modify(reviewId, reviewSaveRequest, mainFile, subFiles);

        model.addAttribute("reviewResponse", reviewResponse);
//        return "redirect:/trendpick/review/" + reviewId;
        return rq.redirectWithMsg("/trendpick/review/" + reviewId, reviewResponse);
    }


    @GetMapping("/list")
    public String showAllReview(Pageable pageable, Model model){
        Page<ReviewResponse> reviewResponses = reviewService.showAll(pageable);
        model.addAttribute("reviewResponses", reviewResponses);

        if (rq.checkLogin() && !rq.checkAdmin()) {
            Member checkMember = rq.getMember();
            String currentUser = checkMember.getUsername();
            model.addAttribute("currentUser", currentUser);
        }
        return "trendpick/review/list";
    }

    @GetMapping("/user")
    public String showOwnReview(Pageable pageable, Model model){
        Member checkMember = rq.getLogin();
        String writer = checkMember.getUsername();
        Page<ReviewResponse> reviewResponses = reviewService.showOwnReview(writer, pageable);
        model.addAttribute("reviewResponses", reviewResponses);
        model.addAttribute("currentUser", writer);
        return "trendpick/review/list";
    }
}