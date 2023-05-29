package project.trendpick_pro.domain.cart.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import project.trendpick_pro.domain.cart.entity.Cart;

@Getter
public class CartResponse {
    private Long id;

    private Long cartItemId;

    private Long productOptionId;

    private int count;
    private int price;

    @Builder
    @QueryProjection
    public CartResponse(Long id, Long cartItemId, long productOptionId, int price, int count) {
        this.id = id;
        this.cartItemId = cartItemId;
        this.productOptionId=productOptionId;
        this.price=price;
        this.count = count;
    }

}
