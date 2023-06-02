package project.trendpick_pro.domain.recommend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.dto.response.ProductByRecommended;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.recommend.entity.Recommend;
import project.trendpick_pro.domain.recommend.entity.dto.RecommendResponse;
import project.trendpick_pro.domain.recommend.repository.RecommendRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {
    private final ProductRepository productRepository;
    private final RecommendRepository recommendRepository;
    private final ProductService productService;
    private final MemberRepository memberRepository;
    private final Rq rq;
    //recommend -> 태그 기반 추천 상품들이 있어야 함

    @Transactional
    @Scheduled(cron = "* * * 4 * *")    //새벽 4시에 한 번씩 작동
    public void select(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        recommendRepository.deleteAll();

        //추천상품 ID값을 가지고 있는 ProductByRecommended 가져오기 (전달용)
        List<ProductByRecommended> recommendProductList = productService.getRecommendProduct(member);
        List<Product> products = new ArrayList<>();
        for (ProductByRecommended productByRecommended : recommendProductList) {
            Product product = productRepository.findById(productByRecommended.getProductId()).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
            products.add(product);
        }

        for (Product product : products) {
            Recommend recommend = Recommend.of(product);
            recommend.connectProduct(product);
            recommend.connectMember(member);
            recommendRepository.save(recommend);
        }
    }

    public Page<RecommendResponse> getFindAll(int offset){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PageRequest pageable = PageRequest.of(offset, 18);
        return recommendRepository.findAllByMemberName(username, pageable);
    }
}
