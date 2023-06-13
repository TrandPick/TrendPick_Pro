package project.trendpick_pro.domain.cart.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.entity.dto.response.CartItemResponse;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.global.rsData.RsData;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/usr/cart")
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;

    private final Rq rq;

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/list")
    public String showCart( Model model) {
        Member member = rq.CheckMember().get();
        Cart carts = cartService.getCartByUser(member.getId());
        List<CartItem> cartItems = cartService.CartView(carts);
        model.addAttribute("cartItems", cartItems);
        return "/trendpick/usr/cart/list";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/add")
    @ResponseBody
    public String addItemToCart(CartItemRequest cartItemRequests, Model model) {
        model.addAttribute("cartItemRequest", cartItemRequests);
        return "/trendpick/usr/cart/add";
    }


    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/add")
    @ResponseBody
    public String addItem(@ModelAttribute @Valid CartItemRequest cartItemRequests, Model model) {
        RsData<CartItemResponse> cartItemResponse = cartService.addItemToCart(rq.CheckMember().get(), cartItemRequests);
       if(cartItemResponse.isFail()){
           rq.redirectWithMsg("/trendpick/products/list?main-category=상의",cartItemResponse);
       }
        model.addAttribute("cartItemResponse", cartItemResponse);
        return rq.redirectWithMsg("/trendpick/usr/cart/list", "상품이 추가되었습니다.");
    }


    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("delete/{cartItemId}")
    public String removeItem(@PathVariable("cartItemId") Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return rq.redirectWithMsg("/trendpick/usr/cart/list", "상품이 삭제되었습니다.");
    }

    // 장바구니에서 수량 변경
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/update")
    public String updateCount(@RequestParam("cartItemId") Long cartItemId,
                              @RequestParam("quantity") int newQuantity) {
        RsData<CartItem> cartItems=  cartService.updateItemCount(cartItemId, newQuantity);
        if(cartItems.isFail()){
            rq.redirectWithMsg("/trendpick/usr/cart/list",cartItems);
        }
        return "redirect:/trendpick/usr/cart/list";
    }
}