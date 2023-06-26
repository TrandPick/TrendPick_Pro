package project.trendpick_pro.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductByRecommended;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponseBySeller;

import java.util.List;

public interface ProductRepositoryCustom {
    public Page<ProductListResponse> findAllByCategoryId(ProductSearchCond cond, Pageable pageable);
    public List<ProductByRecommended> findRecommendProduct(String username);

    public Page<ProductListResponseBySeller> findAllBySeller(String brand, Pageable pageable);

    public Page<ProductListResponse> findAllByKeyword(ProductSearchCond cond, Pageable pageable);
}
