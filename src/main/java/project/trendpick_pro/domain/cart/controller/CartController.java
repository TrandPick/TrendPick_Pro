package project.trendpick_pro.domain.cart.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.member.entity.Member;


@Slf4j
@Controller
@RequestMapping("/trendpick/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public String addItemToCart(@RequestParam("productOptionId") Long productOptionId,
                                @RequestParam("count") int count,
                                HttpSession session) {
       Member member = (Member) session.getAttribute("member");
        cartService.addItemToCart(member, productOptionId, count);
        return "redirect:/trendpick/cart";
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam("cartItemId") Long cartItemId,
                                     HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        cartService.removeItemFromCart(member, cartItemId);
        return "redirect:/trendpick/cart";
    }

    @PostMapping("/update")
    public String updateCartItemQuantity(@RequestParam("cartItemId") Long cartItemId,
                                         @RequestParam("count") int count,
                                         HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        cartService.updateItemCount(member, cartItemId, count);
        return "redirect:/trendpick/cart";
    }
}