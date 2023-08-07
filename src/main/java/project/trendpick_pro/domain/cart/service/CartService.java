package project.trendpick_pro.domain.cart.service;

import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

public interface CartService {
    List<CartItem> getAllCartItems(Cart cart);
    RsData<CartItem> addCartItem(Member member, CartItemRequest cartItemRequest);
    RsData<CartItem> updateCartItemCount(Long cartItemId, int quantity);
    void deleteCartItem(Long cartItemId);
    Cart getCartByUser(Long memberId);
    void deleteCartItemsByMember(Order order);
    List<CartItem> getSelectedCartItems(Member member, CartToOrderRequest request);
}
