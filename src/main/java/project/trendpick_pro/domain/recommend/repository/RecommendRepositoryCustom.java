package project.trendpick_pro.domain.recommend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.recommend.entity.dto.RecommendResponse;

public interface RecommendRepositoryCustom {
    Page<RecommendResponse> findAllByMemberName(String username, Pageable pageable);
}
