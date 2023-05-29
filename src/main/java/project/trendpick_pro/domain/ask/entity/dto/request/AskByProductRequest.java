package project.trendpick_pro.domain.ask.entity.dto.request;

import lombok.Getter;

@Getter
public class AskByProductRequest {
    private Long productId;

    public AskByProductRequest(Long productId) {
        this.productId = productId;
    }
}
