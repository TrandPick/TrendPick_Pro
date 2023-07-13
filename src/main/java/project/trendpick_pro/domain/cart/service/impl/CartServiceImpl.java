package project.trendpick_pro.domain.cart.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.entity.dto.response.CartItemResponse;
import project.trendpick_pro.domain.cart.repository.CartItemRepository;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.TagType;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final ProductService productService;
    private final FavoriteTagService favoriteTagService;

    @Transactional
    @Override
    public RsData<CartItemResponse> addCartItem(Member member, CartItemRequest cartItemRequest) {

        Cart cart = cartRepository.findByMemberId(member.getId());
        Product product = productService.findById(cartItemRequest.getProductId());

        if (product == null) {
            return RsData.of("F-1", "해당상품을 찾을 수 없습니다.");
        }

        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (cartItem != null) {
            cartItem.addCount(cartItemRequest.getQuantity());
            cart.updateCount(cart.getTotalCount() + 1);
        } else {
            cartItem = CartItem.of(cart, product, cartItemRequest);
            cart.updateCount(cart.getTotalCount() + 1);
            cartItemRepository.save(cartItem);
        }

        favoriteTagService.updateTag(member, product, TagType.CART);
        return RsData.of("S-1", "상품이 추가되었습니다.", CartItemResponse.of(cartItem));
    }

    @Transactional
    @Override
    public RsData<CartItem> updateCartItem(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (cartItem == null) {
            return RsData.of("F-1", "해당 상품은 장바구니에 없습니다.");
        }
        cartItem.update(quantity);
        return RsData.of("S-1", "상품 수량이 업데이트 되었습니다.", cartItem);
    }


    @Transactional
    @Override
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if(cartItem != null){
            Cart cart = cartItem.getCart();
            cart.updateCount(cart.getTotalCount() - 1);
        }
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CartItem> currentCartItems(Cart cart) {
        return cartItemRepository.findAllByCart(cart);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CartItem> currentCartItems(Member member, CartToOrderRequest request) {
        Cart cart = getCartByUser(member.getId());
        List<CartItem> cartItemList = new ArrayList<>();

        for (Long id : request.getSelectedItems()) {
            for (CartItem item : cartItemRepository.findByProductId(id)) {
                if (Objects.equals(item.getCart().getId(), cart.getId())) {
                    cartItemList.add(item);
                }
            }
        }
        return cartItemList;
    }

    @Override
    @Transactional(readOnly = true)
    public Cart getCartByUser(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    @Transactional
    @Override
    public void deleteCartItemsByMember(Order order) {
        cartItemRepository.deleteAllInBatch(order.getCartItems());
    }
}

