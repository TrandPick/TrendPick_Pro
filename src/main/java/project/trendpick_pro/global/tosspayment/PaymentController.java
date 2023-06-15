package project.trendpick_pro.global.tosspayment;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.rsData.RsData;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    private final Rq rq;

    @GetMapping(value = "/payment/{Id}/success")
    public synchronized String paymentResult(
            @PathVariable("Id") Long ProOrderId,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) throws Exception {

        ResponseEntity<JsonNode> responseEntity = paymentService.request(paymentKey, orderId, amount, ProOrderId);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return rq.redirectWithMsg("/trendpick/orders/%s".formatted(ProOrderId), "주문이 완료되었습니다.");
        } else {
            orderService.cancel(ProOrderId);
            return rq.historyBack(RsData.of("F-1", "주문이 완료되지 않았습니다."));
        }
    }

    @GetMapping(value = "/payment/fail")
    public String paymentResult(
            Model model,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "code") Integer code
    ) throws Exception {

        model.addAttribute("code", code);
        model.addAttribute("message", message);

        return "trendpick/orders/order-form";
    }
}
