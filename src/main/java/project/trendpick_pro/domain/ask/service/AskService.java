package project.trendpick_pro.domain.ask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.repository.AskRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.global.rsData.RsData;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AskService {

    private final AskRepository askRepository;
    private final ProductRepository productRepository;

    //상품 상세에서 문의 내역 볼 때
    public Page<AskResponse> showAsksByProduct(Long productId, int offset) {
        Pageable pageable = PageRequest.of(offset, 10);
        return askRepository.findAllByProductId(productId, pageable);
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
    public RsData<Long> delete(Member member, Long askId) {
        Ask ask = askRepository.findById(askId).orElseThrow();
        Long productId = ask.getProduct().getId();

        if(!validateAccess(member, ask))
            return RsData.of("F-1", "해당 문의에 대한 삭제 권한이 없습니다.");

        askRepository.delete(ask);
        return RsData.of("S-1", "상품 문의글이 삭제되었습니다.", productId);
    }

    @Transactional
    public RsData<AskResponse> modify(Member member, Long askId, AskForm askForm) {
        Ask ask = askRepository.findById(askId).orElseThrow();

        if(!validateAccess(member, ask))
            return RsData.of("F-1", "해당 문의에 대한 수정 권한이 없습니다.");

        ask.update(askForm);
        return RsData.of("S-1", "상품 문의글 수정이 정상적으로 처리되었습니다.", AskResponse.of(ask));
    }

    @Transactional
    public RsData<Long> register(Member member, AskForm askForm) {
        Product product = productRepository.findById(askForm.getProductId()).orElseThrow(null);
        if(product == null)
            return RsData.of("F-1", "해당 상품은 존재하지 않습니다.");

        Ask savedAsk = askRepository.save(Ask.of(member, product, askForm));
        return RsData.of("S-1", product.getName()+"에 대한 문의가 등록되었습니다.", savedAsk.getId());
    }


    private boolean validateAccess(Member member, Ask ask) {
        return Objects.equals(ask.getAuthor().getId(), member.getId());
    }
}