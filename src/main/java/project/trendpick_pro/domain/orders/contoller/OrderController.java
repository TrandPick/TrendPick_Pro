package project.trendpick_pro.domain.orders.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderForm;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.orders.service.OrderService;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/orders")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;

    @GetMapping("/order")
    public  String order(OrderForm orderForm, Model model){
        MemberInfoDto memberInfo = new MemberInfoDto("member1", "email", "000", "서울");
        List<OrderItemDto> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemDto("상품1", 5, 500));
        orderItems.add(new OrderItemDto("상품2", 1, 100));
        orderItems.add(new OrderItemDto("상품3", 2, 200));

        orderForm = new OrderForm(memberInfo, orderItems);
        model.addAttribute("orderForm", orderForm);

        return "trendpick/orders/order";
    }

    @PostMapping("/order")
    @ResponseBody
    public synchronized String processOrder(@ModelAttribute("orderForm") OrderForm orderForm,
                                            @RequestParam("paymentMethod") String paymentMethod) {


        System.out.println("회원 이름: " + orderForm.getMemberInfo().getName());
        System.out.println("이메일: " + orderForm.getMemberInfo().getEmail());
        System.out.println("주문 아이템 목록:");
        for (OrderItemDto orderItem : orderForm.getOrderItems()) {
            System.out.println("상품명: " + orderItem.getProductName());
            System.out.println("수량: " + orderItem.getCount());
            System.out.println("가격: " + orderItem.getPrice());
        }
        System.out.println("결제 수단: " + paymentMethod);

        return "주문성공";
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
