package project.trendpick_pro.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewCreateRequest;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewUpdateRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.repository.ReviewRepository;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;


    public ReviewResponse showReview(Long productId) {
        Review review = reviewRepository.findById(productId).orElseThrow();

        return ReviewResponse.of(review);
    }


    public void delete(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public ReviewResponse createReview(Member actor, Long productId, ReviewCreateRequest reviewCreateRequest) {
        Product product = productRepository.findById(productId).orElseThrow();

        Review review = Review.of(reviewCreateRequest, actor, product);
        return ReviewResponse.of(review);
    }

    public ReviewResponse modify(Long reviewId, ReviewUpdateRequest reviewUpdateRequest) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        review.update(reviewUpdateRequest);
        return ReviewResponse.of(review);
    }
}
