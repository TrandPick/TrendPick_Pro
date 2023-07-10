package project.trendpick_pro.domain.cart.service;

import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.entity.dto.response.CartItemResponse;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.global.rsData.RsData;

import java.util.List;

public interface CartService {
    List<CartItem> CartView(Cart cart);
    RsData<CartItemResponse> addItemToCart(Member member, CartItemRequest cartItemRequest);
    RsData<CartItem> updateItemCount(Long cartItemId, int quantity);
    void removeItemFromCart(Long cartItemId);
    Cart getCartByUser(Long memberId);
    void deleteCartItemsByOrder(Order order);
    List<CartItem> findCartItems(Member member, CartToOrderRequest request);
}
