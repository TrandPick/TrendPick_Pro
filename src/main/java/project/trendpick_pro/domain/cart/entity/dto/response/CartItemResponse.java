package project.trendpick_pro.domain.cart.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.cart.entity.CartItem;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItemResponse {
    private Long id;
    private int count;

    @Builder
    @QueryProjection
    public CartItemResponse(Long id, int count) {
        this.id = id;
        this.count = count;
    }

    public static CartItemResponse of(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .count(cartItem.getQuantity())
                .build();
    }
}