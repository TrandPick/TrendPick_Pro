package project.trendpick_pro.domain.recommend.service;

import org.springframework.data.domain.Page;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;

public interface RecommendService {
    void rankRecommend(Member member);
    Page<ProductListResponse> getFindAll(Member member, int offset);
    void rankRecommendFirst(Member member);
}
