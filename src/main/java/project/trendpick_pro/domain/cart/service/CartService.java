package project.trendpick_pro.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.entity.dto.request.CartRequest;
import project.trendpick_pro.domain.cart.repository.CartItemRepository;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.ProductOption;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.repository.ProductOptionRepository;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tag.entity.Tag;
import project.trendpick_pro.domain.tag.entity.type.TagType;
import project.trendpick_pro.domain.tag.service.TagService;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final TagService tagService;

    public List<Cart> findByCartMember(Member member){
        return cartRepository.findByCartMember(member);
    }

    @Transactional
    public void addItemToCart(Member member, Long productId, CartItemRequest cartItemRequest) {
        member=memberRepository.findById(member.getId()).orElseThrow(()->new MemberNotFoundException("존재하지 않는 유저입니다."));
        Cart cart = cartRepository.findByMemberId(member.getId());
        Product product=productRepository.findById(productId).orElseThrow(() ->  new ProductNotFoundException("존재하지 않는 상품 입니다."));

        if(cart==null){
            // 장바구니가 비어있다면 생성
            cart=Cart.createCart(member);
            cartRepository.save(cart);
        }
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        tagService.updateTag(member, product, TagType.CART); //장바구니에 넣었으니 해당 상품이 가진 태그점수 올리기

        if (cartItem != null) {
            // 이미 카트에 해당 상품이 존재하는 경우, 수량을 증가
            cartItem.addCount(cartItem.getCount());
        } else {
            // 카트에 해당 상품이 없는 경우, 새로운 카트 아이템을 생성하여 추가
            cartItem =CartItem.createCartItem(cart,product,cartItemRequest);
            cartItemRepository.save(cartItem);
            cart.setTotalCount(cart.getTotalCount()+1);
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

    private Product getProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
    }
}