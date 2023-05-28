package project.trendpick_pro.domain.orders.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSaveRequest;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.service.OrderService;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public String order(@Valid OrderSaveRequest... orderSaveRequests) throws IllegalAccessException {

        orderService.order(1L, orderSaveRequests);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(Model model, @Valid OrderSearchCond orderSearchCond) {
        Page<Order> responses = orderService.findAll(orderSearchCond);

        model.addAttribute("orders", responses);
        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancel(orderId);
        return "redirect:/orders";
    }

}
