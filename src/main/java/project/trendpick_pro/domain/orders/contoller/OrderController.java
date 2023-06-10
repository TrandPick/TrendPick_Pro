package project.trendpick_pro.domain.orders.contoller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderForm;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.form.ProductOptionForm;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/orders")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;
    private final MemberService memberService;
    private final ProductRepository productRepository;
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/order-form")
    public String orderForm(@ModelAttribute OrderForm orderForm,
                            HttpServletRequest req,
                            Model model) {
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(req);
        if (flashMap != null) {
            orderForm = (OrderForm) flashMap.get("orderForm");
            model.addAttribute("orderForm", orderForm);
        }
        model.addAttribute("orderForm", orderForm);
        return "trendpick/orders/order-form";
    }
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/order")
    public synchronized String processOrder(@ModelAttribute("orderForm") OrderForm orderForm) {
        Member member = rq.CheckMember().get();
        if (member.getId() != orderForm.getMemberInfo().getMemberId())
            throw new RuntimeException("잘못된 접근입니다.");

        orderService.order(member, orderForm);
        return "redirect:/trendpick/orders/list";
    }
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/cart")
    public String cartToOrder(@RequestParam("selectedItems") List<Long> selectedItems, RedirectAttributes redirect) {
        redirect.addFlashAttribute("orderForm"
                ,orderService.cartToOrder(rq.CheckMember().get(), selectedItems));

        return "redirect:/trendpick/orders/order-form";
    }
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/order/product")
    public String orderProduct(@ModelAttribute ProductOptionForm productOptionForm, RedirectAttributes redirect) {
        redirect.addFlashAttribute("orderForm", orderService.productToOrder(rq.CheckMember().get(), productOptionForm));
        return "redirect:/trendpick/orders/order-form";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/list")
    public String orderListByMember(
            @RequestParam(value = "page", defaultValue = "0") int offset,
            Model model) {
        Page<OrderResponse> orderList = orderService.findAllByMember(rq.CheckMember().get(), offset);
        int blockPage = 5;
        int startPage = (offset / blockPage) * blockPage + 1;
        int endPage = Math.min(startPage + blockPage - 1, orderList.getTotalPages());

        model.addAttribute("orderList", orderList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "trendpick/usr/member/orders";
    }
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancel(orderId);
        return "redirect:trendpick/usr/member/orders";
    }
}
