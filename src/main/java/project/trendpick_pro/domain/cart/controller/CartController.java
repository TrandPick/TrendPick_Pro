package project.trendpick_pro.domain.cart.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSaveRequest;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/cart")
public class CartController {
    private final CartService cartService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showCart(Model model) {
       List<Cart> carts=cartService.findByCartMember(rq.getMember());
       model.addAttribute("carts",carts);
        return "redirect:/trendprick/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/add/{productId}")
    public String addItemToCart(@PathVariable("productId") Long productId, @Valid CartItemRequest cartItemRequests) {
        cartService.addItemToCart(rq.getMember(),productId,cartItemRequests);
       // 쇼핑을 계속 하시겠습니까? 띄우고 yes이면 main no면 cart로
        return "redirect:/trendprick/list";
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