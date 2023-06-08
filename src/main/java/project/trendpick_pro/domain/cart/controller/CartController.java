package project.trendpick_pro.domain.cart.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/usr/cart")
public class CartController {
    private final CartService cartService;
    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showCart(Model model) {
        Cart carts = cartService.getCartByUser(rq.CheckMember().get().getId());
        List<CartItem> cartItems = cartService.CartView(carts);

        int totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += (cartItem.getProduct().getPrice() * cartItem.getCount());
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "/trendPick/usr/cart/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/add/{productId}")
    public String addItemToCart(@PathVariable("productId") Long productId, CartItemRequest cartItemRequests, Model model) {
        Member member = rq.getMember();
        model.addAttribute("cartItemRequest", cartItemRequests);
        // 쇼핑을 계속 하시겠습니까? 띄우고 yes이면 main no면 cart로
        return "/trendpick/usr/cart/add";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public String addItem(Long productId, @ModelAttribute @Valid CartItemRequest cartItemRequests, Model model) {
        CartItemResponse cartItemResponse = cartService.addItemToCart(rq.CheckMember().get(), productId, cartItemRequests);
        model.addAttribute("cartItemResponse", cartItemResponse);
        // System.out.println(cartItemRequests.getCount());
        // System.out.println(cartItemRequests.getColor());
        // 쇼핑을 계속 하시겠습니까? 띄우고 yes이면 main no면 cart로
        return "redirect:/trendpick/usr/cart/list";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("{memberId}/{cartItemId}")
    public String removeItem(@PathVariable("memberId") Long memberId,@PathVariable("cartItemId") Long cartItemId) {
        Member member = memberService.findByMember(memberId);
        member.getCart().setTotalCount(member.getCart().getTotalCount() - 1);
        cartService.removeItemFromCart(cartItemId);
        return "redirect:/trendpick/usr/cart/list";
    }

    // 장바구니에서 수량 변경
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String updateCount(@RequestParam("cartItemId") Long cartItemId,
                                              @RequestParam("count") int newCount) {
        Member member = rq.getMember();
        cartService.updateItemCount(cartItemId, newCount);
        return "redirect:/trendpick/usr/cart/list";
    }
}