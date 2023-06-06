package project.trendpick_pro.domain.recommend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.dto.response.ProductByRecommended;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.recommend.entity.Recommend;
import project.trendpick_pro.domain.recommend.repository.RecommendRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final ProductService productService;
    private final MemberRepository memberRepository;

    @Value("${file.path}")
    private String filePath;
    //recommend -> 태그 기반 추천 상품들이 있어야 함

    @Transactional
    @Scheduled(cron = "0 0 4 * * *")
    public void select(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 둘다 테스트 해보기
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        recommendRepository.deleteAllByMemberId(member.getId());

        List<Product> products = productService.getRecommendProduct(member);

        for (Product product : products) {
            Recommend recommend = Recommend.of(product);
            recommend.connectProduct(product);
            recommend.connectMember(member);
            recommendRepository.save(recommend);
        }
    }

    public Page<ProductListResponse> getFindAll(int offset){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PageRequest pageable = PageRequest.of(offset, 18);
        Page<ProductListResponse> listResponses = recommendRepository.findAllByMemberName(username, pageable);

        List<ProductListResponse> list = listResponses.getContent().stream()
                .peek(product -> {
                    String updatedMainFile = filePath + product.getMainFile();
                    product.setMainFile(updatedMainFile);
                }).toList();

        return new PageImpl<>(list, pageable, listResponses.getTotalElements());
    }
}
