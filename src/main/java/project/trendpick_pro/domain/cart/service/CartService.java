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
        int totalCount=0;
        // 장바구니가 비어있는 경우
        if (cart == null) {
            totalCount=0;
            return userItems;
        }
        for (CartItem cartItem : cartItems) {
            totalCount=cart.getTotalCount();
            if (cartItem.getCart().getId() == cart.getId()) {
                userItems.add(cartItem);
                totalCount+=cartItem.getQuantity();
            }
        }
        cart.update(totalCount);
        return userItems;
    }

    // 장바구니 상품 추가
    @Transactional
    public CartItemResponse addItemToCart(Member member, CartItemRequest cartItemRequest) {
        Cart cart = cartRepository.findByMemberId(member.getId());
        Product product = getProductById(cartItemRequest.getProductId());

        if (cart == null) {
            // 장바구니가 비어있다면 생성
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        favoriteTagService.updateTag(member, product, TagType.CART); //장바구니에 넣었으니 해당 상품이 가진 태그점수 올리기

        if (cartItem != null) {
            // 이미 카트에 해당 상품이 존재하는 경우, 수량을 증가
            cartItem.addCount(cartItemRequest.getQuantity());
            cart.setTotalCount(cart.getTotalCount() + cartItemRequest.getQuantity());
        } else {
            // 카트에 해당 상품이 없는 경우, 새로운 카트 아이템을 생성하여 추가
            cartItem = CartItem.of(cart, product, cartItemRequest);
            cart.setTotalCount(cart.getTotalCount() + 1);
            cartItemRepository.save(cartItem);
        }
        return CartItemResponse.of(cartItem);
    }


    // 상품을 장바구니에서 제거
    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // 상품의 수량 업데이트
    @Transactional
    public void updateItemCount(Member member, Long cartItemId, int quantity) {
        Cart cart = cartRepository.findByMemberId(member.getId());
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);

        // quantity값이 수량에 있는 값(input) 그대로 넘어옴
        // 수량이 1로 들어오는 게 아닌 해당 상품의 수량 값이 오기 때문에
        // 기존 해당 아이템의 수량값을 빼줌
        cart.update(cart.getTotalCount() + (quantity - cartItem.getQuantity()));
        cartItem.update(quantity);
    }

    public Cart getCartByUser(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
    }

    public List<CartItem> findCartItems(Member member,List<Long> cartItemIdList)  {
        Cart cart=getCartByUser(member.getId()); //현재 로그인되어 있는 cart 정보
        List<CartItem> cartItemList = new ArrayList<>();

        for (Long id : cartItemIdList) {
            for(CartItem item : cartItemRepository.findByProductId(id)) {
                if(item.getCart().getId() == cart.getId()) {
                    cartItemList.add(item);
                }
            }
        }
        return cartItemList;
    }

    @Transactional
    public void deleteCartItemsByOrder(Member member,List<Long> cartItemIdList) {
       Cart cart = cartRepository.findByMemberId(member.getId());
       int quantity=0;
       for(long id: cartItemIdList){
           CartItem cartItem = cartItemRepository.findById(id).orElse(null);
           quantity+= cartItem.getQuantity();
       }
       cartItemRepository.deleteAllByIdInBatch(cartItemIdList);
       cart.update(cart.getTotalCount()-quantity);
    }
}

