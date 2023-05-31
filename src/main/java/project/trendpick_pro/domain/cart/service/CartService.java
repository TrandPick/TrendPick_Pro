package project.trendpick_pro.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartRequest;
import project.trendpick_pro.domain.cart.entity.dto.response.CartResponse;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.ProductOption;
import project.trendpick_pro.domain.product.repository.ProductOptionRepository;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductOptionRepository productOptionRepository;

    public List<Cart> findByCartMember(Member member){
        return cartRepository.findByCartMemberId(member);
    }
    public Cart createCart(Member member) {
        Cart cart = new Cart(member);
        return cartRepository.save(cart);
    }

    public void addItemToCart(Member member, Long productOptionId, int count) {
        Cart cart = getCartByUser(member);
        ProductOption productOption = getProductOptionById(productOptionId);

        CartItem cartItem = cart.findCartItemByProductOption(productOption);


        if (cartItem != null) {
            // 이미 카트에 해당 상품이 존재하는 경우, 수량을 증가
            cartItem.setCount(cartItem.getCount() + count);
        } else {
            // 카트에 해당 상품이 없는 경우, 새로운 카트 아이템을 생성하여 추가
            cartItem = new CartItem(cart, productOption, count);
            cart.addItem(cartItem);
        }
    }

    // 상품을 장바구니에서 제거
    public void removeItemFromCart(Member member, Long cartItemId) {
        Cart cart = getCartByUser(member);
        cart.removeItem(cartItemId);
    }

    // 상품의 수량 업데이트
    public void updateItemCount(Member member, Long cartItemId, int count) {
        Cart cart = getCartByUser(member);
        cart.updateItemCount(cartItemId, count);
    }

    public Cart getCartByUser(Member member) {
        return cartRepository.findByMemberId(member.getId());
    }

    private ProductOption getProductOptionById(Long productOptionId) {
        return productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다."));
    }
}
