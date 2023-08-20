package project.trendpick_pro.global.tosspayment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.notification.service.NotificationService;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;
import project.trendpick_pro.global.tosspayment.dto.PaymentResultResponse;
import project.trendpick_pro.global.tosspayment.service.PaymentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final NotificationService notificationService;

    private final Rq rq;

    @Transactional
    @GetMapping(value = "/{id}/success")
    public String PaymentTry(
            @PathVariable("id") Long id,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) {

        PaymentResultResponse response = paymentService.requestPayment(paymentKey, orderId, amount);
        Order order = orderService.findById(id);

        if (response.getStatus().equals("DONE")) {
            order.connectPayment(
                    "TossPayments" + response.getMethod(),
                    response.getPaymentKey()
            );
            notificationService.create(rq.getMember(), order.getId());
            return rq.redirectWithMsg("/trendpick/orders/%s".formatted(id), "주문이 완료되었습니다.");
        } else {
            return rq.historyBack(RsData.of("F-1", "주문이 완료되지 않았습니다."));
        }
    }

    @GetMapping(value = "/{id}/cancel")
    public String paymentCancel(@PathVariable("id") Long id) {
        orderService.cancel(id);
        notificationService.create(rq.getMember(),id);
        paymentService.cancelPayment(orderService.findById(id).getPaymentKey());
        return rq.redirectWithMsg("/trendpick/orders/usr", "주문을 취소했습니다.");
    }
}