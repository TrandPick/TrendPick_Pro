package project.trendpick_pro.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByWriter(String writer, Pageable pageable);
}
