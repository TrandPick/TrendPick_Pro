package project.trendpick_pro.domain.orders.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.orders.service.OrderService;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/order")
    public  String order(@ModelAttribute MemberInfoDto memberInfo,
                         @ModelAttribute List<OrderItemDto> orderItems,
                         Model model){


        model.addAttribute("orderItems", orderItems);
        model.addAttribute("memberInfo", memberInfo);

        return "trendpick/orders/order";
    }

    @PostMapping("/order")
    public synchronized String order(){


        return "redirect:/orders";
    }

    @GetMapping("/list")
    public String orderList(Model model) {
//        Page<OrderResponse> responses = orderService.findAll(1L);
//        model.addAttribute("orders", responses);
        return "trendpick/usr/member/orders";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancel(orderId);
        return "redirect:trendpick/usr/member/orders";
    }
}
