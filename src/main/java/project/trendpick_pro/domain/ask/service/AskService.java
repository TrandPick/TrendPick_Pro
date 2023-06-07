package project.trendpick_pro.domain.ask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.repository.AskRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AskService {

    private final AskRepository askRepository;
    private final ProductRepository productRepository;

    //상품 상세에서 문의 내역 볼 때
    public Page<AskResponse> showAsksByProduct(Long productId, int offset) {
        Pageable pageable = PageRequest.of(offset, 10);
        Page<Ask> asks = askRepository.findAllByProductId(productId, pageable);
        return AskResponse.of(asks);
    }

    public Page<AskResponse> showAsksByMyPage(Member member, int offset) {
        Pageable pageable = PageRequest.of(offset, 10);
        Page<Ask> asks = askRepository.findAllByMember(member, pageable);
        return AskResponse.of(asks);
    }

    public AskResponse show(Long askId) {
        Ask ask = askRepository.findById(askId).orElseThrow();
        return AskResponse.of(ask);
    }

    @Transactional
    public void delete(Member member, Long askId) {
        Ask ask = askRepository.findById(askId).orElseThrow();

        if(validateAccess(member, ask))
            throw new RuntimeException("해당 문의에 대한 삭제 권한이 없습니다.");

        askRepository.delete(ask);
    }

    @Transactional
    public AskResponse modify(Member member, Long askId, AskRequest askRequest) {
        Ask ask = askRepository.findById(askId).orElseThrow();

        if(validateAccess(member, ask))
            throw new RuntimeException("해당 문의에 대한 수정 권한이 없습니다.");

        ask.update(askRequest);
        return AskResponse.of(ask);
    }

    @Transactional
    public AskResponse register(Member member, AskForm askForm) {
        Product product = productRepository.findById(askForm.getProductId()).orElseThrow();
        Ask ask = Ask.of(member, product, askForm);

        askRepository.save(ask);
        return AskResponse.of(ask);
    }


    private boolean validateAccess(Member member, Ask ask) {
        if(ask.getAuthor().getId() == member.getId())
            return true;
        return false;
    }
}
