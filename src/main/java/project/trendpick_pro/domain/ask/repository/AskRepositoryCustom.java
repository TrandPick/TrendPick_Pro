package project.trendpick_pro.domain.ask.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.ask.entity.dto.request.AskByProductRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskByProductResponse;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;

public interface AskRepositoryCustom {

    public Page<AskByProductResponse> findAllByProduct(AskByProductRequest request, Pageable pageable);
}
