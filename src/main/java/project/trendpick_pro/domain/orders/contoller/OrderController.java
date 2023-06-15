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
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderForm;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.form.ProductOptionForm;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.global.rsData.RsData;

import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/orders")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;
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
        int totalPrice = 0;
        for (OrderItemDto item : orderForm.getOrderItems()) {
            totalPrice += item.getPrice() * item.getCount();
        }
        model.addAttribute("orderId", orderService.OrderSize());
        model.addAttribute("orderForm", orderForm);
        model.addAttribute("SuperTotalPrice", totalPrice);
        return "trendpick/orders/order-form";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/order")
    public String cartToOrder(@RequestParam("selectedItems") List<Long> selectedItems, Model model) {

        RsData<Order> order = orderService.cartToOrder(rq.CheckMember().get(), selectedItems);

        if(order.isFail()) {
            return rq.historyBack(order);
        }

        model.addAttribute("order", order.getData());
        return "trendpick/orders/order-form";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/order/product")
    public String orderProduct(@ModelAttribute ProductOptionForm productOptionForm, Model model) {
        List<Long> selectedItems = new ArrayList<>();
        selectedItems.add(productOptionForm.getProductId());
        RsData<Order> order = orderService.cartToOrder(rq.CheckMember().get(), selectedItems);

        if(order.isFail()) {
            return rq.historyBack(order);
        }

        model.addAttribute("order", order.getData());
        return "trendpick/orders/order-form";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("usr/list")
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

    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})")
    @GetMapping("admin/list")
    public String orderListBySeller(
            @RequestParam(value = "page", defaultValue = "0") int offset,
            Model model) {
        Page<OrderResponse> orderList = orderService.findAllBySeller(rq.CheckAdmin().get(), offset);
        int blockPage = 5;
        int startPage = (offset / blockPage) * blockPage + 1;
        int endPage = Math.min(startPage + blockPage - 1, orderList.getTotalPages());
        model.addAttribute("orderList", orderList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "trendpick/admin/sales";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        RsData result = orderService.cancel(orderId);
        if(result.isFail())
            return rq.historyBack(result);
        return rq.redirectWithMsg("/trendpick/orders/usr/list", result);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/{orderId}")
    public String showOrder(@PathVariable("orderId") Long orderId, Model model){
        Member member = rq.CheckMember().get();
        RsData<OrderDetailResponse> result = orderService.showOrderItems(member, orderId);
        if(result.isFail())
            return rq.historyBack(result);

        model.addAttribute("address",
                member.getAddress());
        model.addAttribute("order",
                result.getData());
        return "trendpick/orders/detail";
    }
}