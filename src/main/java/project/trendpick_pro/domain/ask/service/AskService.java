package project.trendpick_pro.domain.ask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.repository.AskRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AskService {

    private final AskRepository askRepository;
    private final ProductRepository productRepository;

    public List<AskResponse> showAsksByProduct(int offset, Long productId) {
        Pageable pageable = PageRequest.of(offset, 10);

        List<AskResponse> responses = new ArrayList<>();

        return responses;
    }

    public AskResponse show(Long askId) {
        Ask ask = askRepository.findById(askId).orElseThrow();

        return AskResponse.of(ask);
    }

    @Transactional
    public void delete(Long askId) {
        askRepository.deleteById(askId);
    }

    @Transactional
    public AskResponse modify(Long askId, AskRequest askRequest) {
        Ask ask = askRepository.findById(askId).orElseThrow();

        ask.update(askRequest);
        return AskResponse.of(ask);
    }

    @Transactional
    public AskResponse register(Member member, Long productId, AskRequest askRequest) {
        Product product = productRepository.findById(productId).orElseThrow();

        Ask ask = Ask.of(member, product, askRequest);

        askRepository.save(ask);
        return AskResponse.of(ask);
    }
}
