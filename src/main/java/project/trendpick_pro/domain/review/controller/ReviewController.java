package project.trendpick_pro.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final MemberRepository memberRepository;
    private final Rq rq;

    @GetMapping("/register")
    public String registerReview() {
        return "/trendpick/review/register";
    }

    @PostMapping("/write")
    public String createReview(@Valid ReviewSaveRequest reviewCreateRequest,
                               @RequestParam("mainFile") MultipartFile mainFile,
                               @RequestParam("subFiles") List<MultipartFile> subFiles, Model model) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 둘다 테스트 해보기
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        //어떤 상품인지 받아와야 함
        ReviewResponse reviewResponse = reviewService.createReview(member, 1L, reviewCreateRequest, mainFile, subFiles);

        model.addAttribute("reviewResponse", reviewResponse);
        return "/trendpick/review/list";

    }

    @GetMapping("/list/{reviewId}")
    public String showReview(@PathVariable Long reviewId, Model model){
        ReviewResponse reviewResponse = reviewService.showReview(reviewId);
        model.addAttribute("reviewResponse", reviewResponse);
        return "redirect:/trendpick/review";
    }

    @GetMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);

        //return "redirect:/trendpick/review/list";
        return "/trendpick/review/list";
    }

    @PostMapping("/edit/{reviewId}")
    public String updateReview(@PathVariable Long reviewId, ReviewSaveRequest reviewSaveRequest, @RequestParam("mainFile") MultipartFile mainFile,
                               @RequestParam("subFiles") List<MultipartFile> subFiles, Model model) throws IOException {
        ReviewResponse reviewResponse = reviewService.modify(reviewId, reviewSaveRequest, mainFile, subFiles);

        model.addAttribute("reviewResponse", reviewResponse);
        return "redirect:/trendpick/review";
    }

//    @GetMapping("/list")
//    public String showAllReview(@RequestParam(value = "page", defaultValue = "0")int offset, Model model){
//        model.addAttribute("reviewListResponse", reviewService.showAll(offset));
//        return "/trendpick/review/list";
//    }

    @GetMapping("/list")
    public String showAllReview(@PageableDefault(size = 8)Pageable pageable, Model model){
        model.addAttribute("reviewResponses", reviewService.showAll(pageable));
        return "/trendpick/review/list";
    }
}
