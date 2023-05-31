package project.trendpick_pro.domain.cart.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import project.trendpick_pro.domain.cart.entity.CartItem;



@Getter
public class CartItemResponse {
    private Long id;

    private Long cartId;

    private Long productOptionId;
    private int count; // 해당 상품 수량

    private int price; // 해당 상품 금액;

    @Builder
    @QueryProjection
    public CartItemResponse(Long id, Long cartId, Long productOptionId, int count, int price) {
        this.id = id;
        this.cartId = cartId;
        this.productOptionId = productOptionId;
        this.count = count;
        this.price = price;
    }

    public static CartItemResponse of (CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .cartId(cartItem.getCart().getId())
                .productOptionId(cartItem.getProductOption().getId())
                .count(cartItem.getCount())
                .price(cartItem.getPrice())
                .build();
    }
}
