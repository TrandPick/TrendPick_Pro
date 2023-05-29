package project.trendpick_pro.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;

public interface ProductRepositoryCustom {
    public Page<ProductListResponse> findAllByCategoryId(ProductSearchCond cond, Pageable pageable);
}
