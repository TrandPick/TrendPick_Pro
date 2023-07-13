package project.trendpick_pro.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewProductResponse;

public interface ReviewRepositoryCustom{
    Page<ReviewProductResponse> findAllByProductId(Long productId, Pageable pageable);
}
