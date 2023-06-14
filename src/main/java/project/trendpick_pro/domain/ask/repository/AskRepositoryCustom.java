package project.trendpick_pro.domain.ask.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.request.AskByProductRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;

public interface AskRepositoryCustom {

    public Page<AskResponse> findAllByProductId(Long productId, Pageable pageable);
}
