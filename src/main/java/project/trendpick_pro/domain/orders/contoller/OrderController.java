package project.trendpick_pro.domain.orders.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderForm;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/orders")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;
    private final MemberService memberService;
    private final ProductRepository productRepository;

    @GetMapping("/order")
    public  String orderForm(@ModelAttribute OrderForm orderForm, Model model){
//        MemberInfoDto memberInfo = new MemberInfoDto(2L, "admin", "admin@naver.com", "000", "서울");
//        List<OrderItemDto> orderItems = new ArrayList<>();
//        orderItems.add(new OrderItemDto(1L, "상품1", 5, 500));
//        orderItems.add(new OrderItemDto(2L, "상품2", 1, 100));
//        orderItems.add(new OrderItemDto(3L, "상품3", 2, 200));
//        orderForm = new OrderForm(memberInfo, orderItems);

        //상품 상세 또는 장바구니에서 OrderForm으로 데이터가 날라오게끔
        model.addAttribute("orderForm", orderForm);

        return "trendpick/orders/order";
    }

    @PostMapping("/order")
    @ResponseBody
    public synchronized String order(@ModelAttribute("orderForm") OrderForm orderForm) {


//        System.out.println("회원 이름: " + orderForm.getMemberInfo().getName());
//        System.out.println("이메일: " + orderForm.getMemberInfo().getEmail());
//        System.out.println("주문 아이템 목록:");
//        for (OrderItemDto orderItem : orderForm.getOrderItems()) {
//            System.out.println("상품명: " + orderItem.getProductName());
//            System.out.println("수량: " + orderItem.getCount());
//            System.out.println("가격: " + orderItem.getPrice());
//        }
//        System.out.println("결제 수단: " + orderForm.getPaymentMethod());

        Member member = memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
//        if(member.getId() != orderForm.getMemberInfo().getMemberId())
//            throw new RuntimeException("잘못된 접근입니다.");

        log.info(member.getUsername());
        log.info(String.valueOf(member.getId()));
        log.info(orderForm.getMemberInfo().getName());
        log.info(orderForm.getOrderItems().get(0).getProductName());
        log.info(String.valueOf(orderForm.getOrderItems().get(0).getProductId()));

        orderService.order(member, orderForm);
        return "주문성공";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String orderListByMember(@RequestParam(value = "page", defaultValue = "0") int offset,
                                    Model model) {
        Member member = memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        Page<OrderResponse> orderList = orderService.findAllByMember(member, offset);
        model.addAttribute("orderList", orderList);
        return "trendpick/usr/member/orders";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancel(orderId);
        return "redirect:trendpick/usr/member/orders";
    }
}
