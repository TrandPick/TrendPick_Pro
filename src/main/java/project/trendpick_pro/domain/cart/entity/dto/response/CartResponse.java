package project.trendpick_pro.domain.cart.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponse {
    private Long id;

    private Long memberId;

    private String productFilePath;
    private String brandName;
    private int totalCount;

    private List<CartItem> cartItems;


    @Builder
    @QueryProjection
    public CartResponse(Long id, Long memberId,  String productFilePath, String brandName,int totalCount,  List<CartItem> cartItems) {
        this.id = id;
        this.memberId = memberId;
        this.productFilePath=productFilePath;
        this.brandName = brandName;
        this.totalCount=totalCount;
        this.cartItems=cartItems;
    }

    public static CartResponse of (Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .memberId(cart.getMember().getId())
                .totalCount(cart.getTotalCount())
                .cartItems(cart.getCartItems())
                .build();
    }
}