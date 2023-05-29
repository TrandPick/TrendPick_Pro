package project.trendpick_pro.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.review.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAll(Long productId);
}
