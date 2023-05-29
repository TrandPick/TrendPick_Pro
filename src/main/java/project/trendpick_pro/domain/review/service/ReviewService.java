package project.trendpick_pro.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.repository.ReviewRepository;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;



    public ReviewResponse showReview(Long productId) {
        Review review = reviewRepository.findById(productId).orElseThrow();

        return ReviewResponse.of(review);
    }


    public void delete(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public ReviewResponse createReview(Member actor, Long productId, ReviewRequest reviewRequest) {
        String username = actor.getUsername();

        Review review = Review.of(username, productId, reviewRequest);
        return ReviewResponse.of(review);
    }

    public ReviewResponse modify(Long reviewId, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        review.update(reviewRequest);
        return ReviewResponse.of(review);
    }
}
