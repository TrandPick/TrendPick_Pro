package project.trendpick_pro.domain.cart.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
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
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final ProductService productService;
    private final FavoriteTagService favoriteTagService;

    private final Rq rq;

    @Transactional
    @Override
    public RsData<CartItem> addCartItem(Member member, CartItemRequest cartItemRequest) {

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

        if (cartItem == null) {
            cartItem = CartItem.of(cart, product, cartItemRequest);
        }

        cartItem.updateCount(cartItemRequest.getQuantity());
        cartItemRepository.save(cartItem);

        favoriteTagService.updateTag(member, product, TagType.CART);
        return RsData.of("S-1", "상품이 추가되었습니다.", cartItem);

    }

        @Transactional
        @Override
        public RsData updateCartItemCount (Long cartItemId,int quantity){
            CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
            if (cartItem == null) {
                return RsData.of("F-1", "해당 상품은 장바구니에 없습니다.");
            }
            cartItem.updateCount(quantity);
            return RsData.of("S-1", "상품 수량이 업데이트 되었습니다.");
        }


        @Transactional
        @Override
        public void deleteCartItem (Long cartItemId){
            CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
            if (cartItem != null) {
                Cart cart = cartItem.getCart();
                cart.updateTotalCount(-1 * cartItem.getQuantity());
            }
            cartItemRepository.deleteById(cartItemId);
        }

        @Transactional(readOnly = true)
        @Override
        public List<CartItem> getAllCartItems(Cart cart){
            return cartItemRepository.findAllByCart(cart);
        }

        @Transactional(readOnly = true)
        @Override
        public List<CartItem> getSelectedCartItems(Member member, CartToOrderRequest request){
            Cart cart = getCartByUser(member.getId());
            List<CartItem> cartItemList = new ArrayList<>();

            for (Long productId : request.getSelectedItems()) {
                CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
                if(cartItem!=null)
                    cartItemList.add(cartItem);
            }
            return cartItemList;
        }

        @Override
        @Transactional(readOnly = true)
        public Cart getCartByUser (Long memberId){
            return cartRepository.findByMemberId(memberId);
        }

    @Transactional
    @Override
    public void deleteCartItemsByMember(Order order) {

        List<CartItem> cartProducts = getCartByUser(rq.getMember().getId()).getCartItems();

        List<String> productCodes = collectProductCode(order);

        List<CartItem> matchedCartItems = matchedCartProducts(cartProducts, productCodes);

        cartItemRepository.deleteAllInBatch(matchedCartItems);
    }

    private static List<CartItem> matchedCartProducts(List<CartItem> cartProducts, List<String> productCodes) {
        return cartProducts.stream()
                .filter(cartItem -> productCodes.contains(cartItem.getProduct().getProductCode()))
                .collect(Collectors.toList());
    }

    private static List<String> collectProductCode(Order order) {
        return order.getOrderItems().stream()
                .map(orderItem -> orderItem.getProduct().getProductCode()).collect(Collectors.toList());
    }
}

