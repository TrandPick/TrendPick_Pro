package project.trendpick_pro.domain.recommend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;

public interface RecommendRepositoryCustom {
    Page<ProductListResponse> findAllByEmail(String username, Pageable pageable);
}
