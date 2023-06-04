package project.trendpick_pro.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.ProductOption;
import project.trendpick_pro.domain.product.repository.ProductOptionRepository;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductOptionRepository productOptionRepository;
    private final FavoriteTagService favoriteTagService;

    public List<Cart> findByCartMember(Member member){
        return cartRepository.findByCartMember(member);
    }
  
    public Cart createCart(Member member) {
        Cart cart = new Cart(member);
        return cartRepository.save(cart);
    }

    @Transactional
    public void addItemToCart(Member member, Long productOptionId, int count) {
        Cart cart = getCartByUser(member);
        ProductOption productOption = getProductOptionById(productOptionId);
        CartItem cartItem = cart.findCartItemByProductOption(productOption);

        favoriteTagService.updateTag(member, productOption.getProduct(), TagType.CART); //장바구니에 넣었으니 해당 상품이 가진 태그점수 올리기

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
                .orElseThrow(() -> new IllegalArgumentException("Product option not found"));
    }
}
