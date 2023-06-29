package project.trendpick_pro.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.entity.dto.response.CartItemResponse;
import project.trendpick_pro.domain.cart.repository.CartItemRepository;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;
import project.trendpick_pro.global.rsData.RsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final ProductService productService;
    private final FavoriteTagService favoriteTagService;


    // 장바구니 조회
    public List<CartItem> CartView(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findAll();
        List<CartItem> userItems = new ArrayList<>();
        // 장바구니가 비어있는 경우
        if (cart == null) {
            return userItems;
        }
        for (CartItem cartItem : cartItems) {
            if (Objects.equals(cartItem.getCart().getId(), cart.getId())) {
                userItems.add(cartItem);
            }
        }
        return userItems;
    }

    // 장바구니 상품 추가
    @Transactional
    public RsData<CartItemResponse> addItemToCart(Member member, CartItemRequest cartItemRequest) {

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
        favoriteTagService.updateTag(member, product, TagType.CART);

        if (cartItem != null) {
            cartItem.addCount(cartItemRequest.getQuantity());
            cart.setTotalCount(cart.getTotalCount() + cartItemRequest.getQuantity());
        } else {
            cartItem = CartItem.of(cart, product, cartItemRequest);
            cart.setTotalCount(cart.getTotalCount() + 1);
            cartItemRepository.save(cartItem);
        }

        return RsData.of("S-1", "상품이 추가되었습니다.", CartItemResponse.of(cartItem));
    }


    // 상품을 장바구니에서 제거
    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // 상품의 수량 업데이트
    @Transactional
    public RsData<CartItem> updateItemCount(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (cartItem == null) {
            return RsData.of("F-1", "해당 상품은 장바구니에 없습니다.");
        }
        cartItem.update(quantity);
        return RsData.of("S-1", "상품 수량이 업데이트 되었습니다.", cartItem);
    }

    public Cart getCartByUser(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    public List<CartItem> findCartItems(Member member, List<Long> cartItemIdList) {
        Cart cart = getCartByUser(member.getId()); //현재 로그인되어 있는 cart 정보
        List<CartItem> cartItemList = new ArrayList<>();

        for (Long id : cartItemIdList) {
            for (CartItem item : cartItemRepository.findByProductId(id)) {
                if (Objects.equals(item.getCart().getId(), cart.getId())) {
                    cartItemList.add(item);
                }
            }
        }
        return cartItemList;
    }

    @Transactional
    public void deleteCartItemsByOrder(Order order) {
        List<CartItem> cartItems = order.getCartItems();
        cartItemRepository.deleteAllInBatch(cartItems);
    }
}

