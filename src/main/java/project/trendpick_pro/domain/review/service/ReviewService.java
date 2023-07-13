package project.trendpick_pro.domain.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewProductResponse;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.global.util.rsData.RsData;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    ReviewResponse getReview(Long productId);
    Review findById(Long id);
    Page<ReviewProductResponse> getReviews(Long productId, Pageable pageable);
    RsData<ReviewResponse> create(Member actor, Long productId, ReviewSaveRequest reviewSaveRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws Exception;
    RsData<ReviewResponse> modify(Long reviewId, ReviewSaveRequest reviewSaveRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException;
    void delete(Long reviewId);
    Page<ReviewResponse> showAll(Pageable pageable);
    Page<ReviewResponse> showOwnReview(String writer, Pageable pageable);
}
