package project.trendpick_pro.global.tosspayment.service;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.TagType;
import project.trendpick_pro.global.tosspayment.dto.PaymentResultResponse;
import project.trendpick_pro.global.util.rsData.RsData;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${toss.secretKey}")
    private String secretKey;

    @Value("${toss.url}")
    private String tossURL;

    private final OrderService orderService;
    private final FavoriteTagService favoriteTagService;

    @Transactional
    public RsData processPayment(String paymentKey, Long orderId, Integer amount, String ORD_ID){
        PaymentResultResponse response = requestPayment(paymentKey, ORD_ID, amount);

        Order order = orderService.findById(orderId);
        validatePayment(order);

        if (response.getStatus().equals("DONE")) {
            successPaymentProcess(response.getMethod(), response.getPaymentKey(), order);
            return RsData.of("S-1", "결제를 성공적으로 완료했습니다.");
        }

        return RsData.of("F-1", "결제 도중에 오류가 발생했습니다.");
    }

    private void successPaymentProcess(String method, String paymentKey, Order order) {
        order.connectPayment("TossPayments" + method, paymentKey);
        for (OrderItem orderItem : order.getOrderItems()) {
            favoriteTagService.updateTag(order.getMember(), orderItem.getProduct(), TagType.ORDER);

            if(orderItem.getCouponCard() != null)
                orderItem.getCouponCard().use(LocalDateTime.now()); //실제 쿠폰 사용 처리
        }
    }

    private PaymentResultResponse requestPayment(String paymentKey, String orderId, Integer amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String secret = new String(Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8)));

        headers.setBasicAuth(secret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", String.valueOf(amount));

        return restTemplate.postForEntity(tossURL + paymentKey, new HttpEntity<>(params, headers), PaymentResultResponse.class).getBody();
    }

    public void cancelPayment(String paymentKey) {

        URI uri = URI.create(tossURL + paymentKey + "/cancel");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String secret = new String(Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8)));

        headers.setBasicAuth(secret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        JSONObject params = new JSONObject();
        params.put("cancelReason", "단순 변심");

        restTemplate.postForObject(
                uri,
                new HttpEntity<>(params, headers),
                PaymentResultResponse.class
        );
    }

    private void validatePayment(Order order) {
        if(order.getPaymentKey() != null)//멱등성
            throw new IllegalArgumentException("이미 결제 완료된 주문입니다.");
    }
}