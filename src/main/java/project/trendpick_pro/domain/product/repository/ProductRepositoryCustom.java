package project.trendpick_pro.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductByRecommended;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponseBySeller;

import java.util.List;

public interface ProductRepositoryCustom {
    Page<ProductListResponse> findAllByCategoryId(ProductSearchCond cond, Pageable pageable);
    List<ProductByRecommended> findRecommendProduct(String email);
    Page<ProductListResponseBySeller> findAllBySeller(String brand, Pageable pageable);
    Page<ProductListResponse> findAllByKeyword(ProductSearchCond cond, Pageable pageable);
}
