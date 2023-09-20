package project.trendpick_pro.domain.cart.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/usr/cart")
public class CartController {

    private final CartService cartService;

    private final Rq rq;

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/list")
    public String showCart(Model model) {
        Cart cart = cartService.getCartByUser(rq.getMember().getId());
        List<CartItem> cartItems = cartService.getAllCartItems(cart);
        model.addAttribute("cartItems", cartItems);
        return "trendpick/usr/cart/list";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/add")
    public String addItem(@ModelAttribute @Valid CartItemRequest cartItemRequests, HttpServletRequest req) {
        RsData<CartItem> result = cartService.addCartItem(rq.getMember(), cartItemRequests);

        if(result.isFail())
           return rq.historyBack(result);

        String referer = req.getHeader("referer");
        return rq.redirectWithMsg(referer, result);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("delete/{cartItemId}")
    public String removeItem(@PathVariable("cartItemId") Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return rq.redirectWithMsg("/trendpick/usr/cart/list", "상품이 삭제되었습니다.");
    }

    // 장바구니에서 수량 변경
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/update")
    public String updateCount(@RequestParam("cartItemId") Long cartItemId,
                              @RequestParam("quantity") int newQuantity) {
        RsData result =  cartService.updateCartItemCount(cartItemId, newQuantity);
        if(result.isFail()){
            rq.redirectWithMsg("/trendpick/usr/cart/list",result);
        }
        return "redirect:/trendpick/usr/cart/list";
    }
}