package project.trendpick_pro.domain.cart.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartResponse {
    private Long id;

    private Long memberId;

    private int totalCount;

    private int totalPrice;

    private List<CartItem> cartItems;


    @Builder
    @QueryProjection
    public CartResponse(Long id, Long memberId, int totalCount, int totalPrice, List<CartItem> cartItems) {
        this.id = id;
        this.memberId = memberId;
        this.totalCount=totalCount;
        this.totalPrice=totalPrice;
        this.cartItems=cartItems;
    }

    public static CartResponse of (Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .memberId(cart.getMember().getId())
                .totalCount(cart.getTotalCount())
                .totalPrice(cart.getTotalPrice())
                .cartItems(cart.getCartItems())
                .build();
    }
}