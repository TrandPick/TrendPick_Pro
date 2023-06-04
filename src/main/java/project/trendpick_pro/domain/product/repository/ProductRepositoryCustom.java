package project.trendpick_pro.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductByRecommended;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    public Page<ProductListResponse> findAllByCategoryId(ProductSearchCond cond, Pageable pageable);
    public List<ProductByRecommended> findRecommendProduct(String username);
}
