package project.trendpick_pro.domain.recommend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.recommend.entity.Recommend;
import project.trendpick_pro.domain.recommend.repository.RecommendRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendRepository recommendRepository;
    private final ProductService productService;
    private final MemberRepository memberRepository;

    @Transactional
    public void select(String username){

        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        recommendRepository.deleteAllByMemberId(member.getId());

        List<Product> products = productService.getRecommendProduct(member);

        for (Product product : products) {
            Recommend recommend = Recommend.of(product);
            recommend.connectMember(member);
            recommendRepository.save(recommend);
        }
    }

    public Page<ProductListResponse> getFindAll(Member member, int offset){
        PageRequest pageable = PageRequest.of(offset, 18);
        Page<ProductListResponse> listResponses = recommendRepository.findAllByMemberName(member.getUsername(), pageable);

        List<ProductListResponse> list = listResponses.getContent().stream()
                .peek(product -> {
                    String updatedMainFile = product.getMainFile();
                    product.setMainFile(updatedMainFile);
                }).toList();

        return new PageImpl<>(list, pageable, listResponses.getTotalElements());
    }
}
