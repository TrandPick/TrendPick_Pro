package project.trendpick_pro.domain.recommend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.recommend.entity.Recommend;
import project.trendpick_pro.domain.recommend.repository.RecommendRepository;
import project.trendpick_pro.domain.recommend.service.RecommendService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final RecommendRepository recommendRepository;
    private final ProductService productService;

    @Transactional
    public void rankRecommend(Member member){
        List<Product> products = productService.getRecommendProduct(member);
        for (Product product : products) {
            Recommend recommend = Recommend.of(product);
            recommend.connectMember(member);
            recommendRepository.save(recommend);
        }
    }

    public Page<ProductListResponse> getFindAll(Member member, int offset){
        PageRequest pageable = PageRequest.of(offset, 18);
        return recommendRepository.findAllByEmail(member.getUsername(), pageable);
    }

    @Transactional
    public void rankRecommendFirst(Member member){
        List<Product> products = productService.getRecommendProduct(member);
        for (Product product : products) {
            Recommend recommend = Recommend.of(product);
            recommend.connectMember(member);
            recommendRepository.save(recommend);
        }
    }
}