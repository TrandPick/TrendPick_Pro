package project.trendpick_pro.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.entity.dto.response.CartItemResponse;
import project.trendpick_pro.domain.cart.repository.CartItemRepository;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final FavoriteTagService favoriteTagService;


    // 장바구니 조회
    public List<CartItem> CartView(Member member, Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findAll();
        List<CartItem> userItems = new ArrayList<>();

        // 장바구니가 비어있는 경우
        if(cart==null){
            return userItems;
        }
        for (CartItem cartItem : cartItems) {
            if (cartItem.getCart().getId() == cart.getId()) {
                userItems.add(cartItem);
            }
        }
        return userItems;
    }

    // 장바구니 상품 추가
    @Transactional
    public CartItemResponse addItemToCart(Member member, Long productId, CartItemRequest cartItemRequest) {
        Cart cart = cartRepository.findByMemberId(member.getId());
        Product product = getProductById(productId);

        if (cart == null) {
            // 장바구니가 비어있다면 생성
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        favoriteTagService.updateTag(member, product, TagType.CART); //장바구니에 넣었으니 해당 상품이 가진 태그점수 올리기

        if (cartItem != null) {
            // 이미 카트에 해당 상품이 존재하는 경우, 수량을 증가
            cartItem.addCount(cartItem.getQuantity());
        } else {
            // 카트에 해당 상품이 없는 경우, 새로운 카트 아이템을 생성하여 추가
            cartItem = CartItem.of(cart, product, cartItemRequest);
            cart.setTotalCount(cart.getTotalCount() + 1);
        }
        cartItemRepository.save(cartItem);
        return CartItemResponse.of(cartItem);
    }


    // 상품을 장바구니에서 제거
    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // 상품의 수량 업데이트
    @Transactional
    public void updateItemCount(Member member,Long cartItemId, int quantity) {
        Cart cart=member.getCart();
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        cart.setTotalCount(cartItem.getQuantity()+quantity);
        cartItem.update(quantity);
        cartItemRepository.save(cartItem);
    }

    public Cart getCartByUser(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
    }

    private Member CheckMember() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        return member;
    }

    public List<CartItem> findCartItems(List<Long> cartItemIdList) {
        List<CartItem> cartItemList = new ArrayList<>();
        for (Long id : cartItemIdList) {
            cartItemList.add(cartItemRepository.findById(id).
                    orElseThrow(() -> new IllegalArgumentException("장바구니에 존재하지 않는 품목입니다.")));
        }
        return cartItemList;
    }
}