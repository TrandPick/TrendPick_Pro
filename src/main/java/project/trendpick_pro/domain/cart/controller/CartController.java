package project.trendpick_pro.domain.cart.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.entity.dto.response.CartItemResponse;
import project.trendpick_pro.domain.cart.entity.dto.response.CartResponse;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSaveRequest;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/usr/cart")
public class CartController {
    private final CartService cartService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showCart(Model model) {
       List<Cart> carts=cartService.findByCartMember(rq.getMember());
       model.addAttribute("carts",carts);
        return "redirect:/trendpick/usr/cart/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/add/{productId}")
    public String addItemToCart(@PathVariable("productId") Long productId,CartItemRequest cartItemRequests,Model model) {
        model.addAttribute("cartItemRequest",cartItemRequests);
        // 쇼핑을 계속 하시겠습니까? 띄우고 yes이면 main no면 cart로
        return "/trendpick/usr/cart/add";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public String addItem(Long productId, @ModelAttribute @Valid CartItemRequest cartItemRequests,Model model) {
        CartItemResponse cartItemResponse=cartService.addItemToCart(productId,cartItemRequests);
        model.addAttribute("cartItemResponse",cartItemResponse);
        // System.out.println(cartItemRequests.getCount());
        // System.out.println(cartItemRequests.getColor());
        // 쇼핑을 계속 하시겠습니까? 띄우고 yes이면 main no면 cart로
        return "/trendpick/usr/cart/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam("cartItemId") Long cartItemId) {
        Member member = rq.getMember();
        cartService.removeItemFromCart(member, cartItemId);
        return "redirect:/trendpick/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String updateCartItemQuantity(@RequestParam("cartItemId") Long cartItemId,
                                         @RequestParam("count") int count) {
        Member member = rq.getMember();
        cartService.updateItemCount(member, cartItemId, count);
        return "redirect:/trendpick/list";
    }
}