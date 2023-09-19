package project.trendpick_pro.global.tosspayment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;
import project.trendpick_pro.global.tosspayment.service.PaymentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final Rq rq;

    @GetMapping(value = "/{id}/success")
    public String PaymentTry(
            @PathVariable("id") Long id,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) {

        RsData result = paymentService.processPayment(paymentKey, id, amount, orderId);

        if (result.isSuccess())
            return rq.redirectWithMsg("/trendpick/orders/%s".formatted(id), "결제를 성공적으로 완료했습니다.");

        orderService.cancel(id);
        return rq.historyBack(RsData.of("F-1", "결제 도중에 오류가 발생했습니다."));

    }

    @Transactional
    @GetMapping(value = "/{id}/fail")
    public String PaymentFail(
            @PathVariable("id") Long id,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam(value = "paymentKey", required = false) String paymentKey) {

        orderService.cancel(id);
        return rq.redirectWithMsg("/trendpick/products/list?main-category=전체", "결제에 실패했습니다.");
    }

    @GetMapping(value = "/{id}/cancel")
    public String paymentCancel(@PathVariable("id") Long id) {
        orderService.cancel(id);
        paymentService.cancelPayment(orderService.findById(id).getPaymentKey());
        return rq.redirectWithMsg("/trendpick/orders/usr", "주문을 취소했습니다.");
    }
}