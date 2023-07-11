package project.trendpick_pro.domain.ask.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.exception.AskNotFoundException;
import project.trendpick_pro.domain.ask.exception.AskNotMatchException;
import project.trendpick_pro.domain.ask.repository.AskRepository;
import project.trendpick_pro.domain.ask.service.AskService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.global.rsData.RsData;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AskServiceImpl implements AskService {

    private final AskRepository askRepository;
    private final ProductService productService;

    @Override
    @Transactional
    public RsData<Long> register(Member member, AskForm askForm) {
        Product product = productService.findById(askForm.getProductId());
        Ask savedAsk = askRepository.save(Ask.of(member, product, askForm));
        return RsData.of("S-1", product.getName()+"에 대한 문의가 등록되었습니다.", savedAsk.getId());
    }

    @Override
    @Transactional
    public RsData<AskResponse> modify(Member member, Long askId, AskForm askForm) {
        Ask ask = getAskWithAuthValidation(member, askId);
        ask.update(askForm);
        return RsData.of("S-1", "상품 문의글 수정이 정상적으로 처리되었습니다.", AskResponse.of(ask));
    }

    @Override
    @Transactional
    public RsData<Long> delete(Member member, Long askId) {
        Ask ask = getAskWithAuthValidation(member, askId);
        askRepository.delete(ask);
        return RsData.of("S-1", "상품 문의글이 삭제되었습니다.", ask.getProduct().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public AskResponse show(Long askId) {
        Ask ask = askRepository.findById(askId)
                .orElseThrow(() -> new AskNotFoundException("해당 문의글은 존재하지 않습니다."));
        return AskResponse.of(ask);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AskResponse> showAsksByProduct(Long productId, int offset) {
        Pageable pageable = PageRequest.of(offset, 10);
        return askRepository.findAllByProductId(productId, pageable);
    }

    private Ask getAskWithAuthValidation(Member member, Long askId) {
        Ask ask = askRepository.findById(askId)
                .orElseThrow(() -> new AskNotFoundException("해당 문의글은 존재하지 않습니다."));
        if (!Objects.equals(ask.getAuthor().getId(), member.getId())) {
            throw new AskNotMatchException("해당 문의에 대한 권한이 없습니다.");
        }
        return ask;
    }
}
