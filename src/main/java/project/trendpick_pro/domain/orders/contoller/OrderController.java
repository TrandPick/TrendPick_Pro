package project.trendpick_pro.domain.orders.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.domain.orders.entity.dto.request.ProductOrderRequest;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/orders")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("{orderId}/form")
    public String showOrderForm(@PathVariable("orderId") Long orderId, Model model) {
        RsData<Order> result = orderService.getOrderFormData(orderId);
        if (result.getResultCode().equals("F-1"))
            return rq.historyBack("주문중 오류가 발생했습니다. 다시 한 번 시도해주세요.");

        if (result.getResultCode().equals("F-2"))
            return rq.redirectWithMsg("/", "이미 처리된 주문입니다.");

        model.addAttribute("order", result.getData());
        return "trendpick/orders/order-form";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/order/cart")
    public String cartToOrder(@RequestBody CartToOrderRequest request) {
        RsData result = orderService.cartToOrder(rq.getMember(), request);
        if (result.isFail())
            return rq.historyBack(result);

        return rq.redirectWithMsg("/trendpick/orders/%s/form".formatted(result.getData()), result);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/order/product")
    public String productToOrder(@RequestBody ProductOrderRequest request) {
        RsData result = orderService.productToOrder(rq.getMember(),
                request.getProductId(), request.getQuantity(), request.getSize(), request.getColor());
        if (result.isFail())
            return rq.historyBack(result);

        return rq.redirectWithMsg("/trendpick/orders/%s/form".formatted(result.getData()), result);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/{orderId}")
    public String showOrder(@PathVariable("orderId") Long orderId, Model model) {
        Member member = rq.getMember();
        RsData<OrderDetailResponse> result = orderService.findOrderItems(member, orderId);

        if (result.isFail()) {
            return rq.historyBack(result);
        }
        model.addAttribute("address", member.getAddress());
        model.addAttribute("order", result.getData());
        return "trendpick/orders/detail";
    }

    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        RsData result = orderService.cancel(orderId);
        if (result.isFail())
            return rq.historyBack(result);
        return rq.redirectWithMsg("/trendpick/orders/usr/list", result);
    }

    //환불 내역
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/usr/refunds")
    public String showCanceledOrderListByMember(@RequestParam(value = "page", defaultValue = "0") int offset, Model model) {
        Page<OrderResponse> orderList = orderService.findCanceledOrders(rq.getMember(), offset);
        model.addAttribute("orderList", orderList);
        return "trendpick/usr/member/refunds";
    }

    //주문 내역
    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("usr")
    public String orderListByMember(@RequestParam(value = "page", defaultValue = "0") int offset, Model model) {
        Page<OrderResponse> orderList = orderService.findAll(rq.getMember(), offset);
        model.addAttribute("orderList", orderList);
        return "trendpick/usr/member/orders";
    }

    //판매 내역
    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})")
    @GetMapping("admin")
    public String orderListBySeller(@RequestParam(value = "page", defaultValue = "0") int offset, Model model) {
        Page<OrderResponse> orderList = orderService.findAll(rq.getAdmin(), offset);
        model.addAttribute("orderList", orderList);
        int totalPricePerMonth = orderService.settlementOfSales(rq.getAdmin(), LocalDate.now());
        model.addAttribute("totalPricePerMonth", totalPricePerMonth);
        return "trendpick/admin/sales";
    }
}