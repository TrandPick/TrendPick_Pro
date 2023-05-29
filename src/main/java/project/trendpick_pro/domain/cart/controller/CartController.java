package project.trendpick_pro.domain.cart.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;


@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/cart")
public class CartController {
    private final CartService cartService;
    private final Rq rq;
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public String addItemToCart(@RequestParam("productOptionId") Long productOptionId,
                                @RequestParam("count") int count) {
        Member member = rq.getMember();
        cartService.addItemToCart(member, productOptionId, count);
       // 쇼핑을 계속 하시겠습니까? 띄우고 yes이면 main no면 cart로
        return "redirect:/trendprick/cart";
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam("cartItemId") Long cartItemId) {
        Member member = rq.getMember();
        cartService.removeItemFromCart(member, cartItemId);
        return "redirect:/trendpick/cart";
    }

    @PostMapping("/update")
    public String updateCartItemQuantity(@RequestParam("cartItemId") Long cartItemId,
                                         @RequestParam("count") int count) {
        Member member = rq.getMember();
        cartService.updateItemCount(member, cartItemId, count);
        return "redirect:/trendpick/cart";
    }
}