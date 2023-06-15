package project.trendpick_pro.global.tosspayment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.service.OrderService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;

    public ResponseEntity<JsonNode> request(String paymentKey, String orderId, Integer amount, Long ProOrderId) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate(); // 스프링 외부 api 요청/응답 클래스
        HttpHeaders headers = new HttpHeaders(); // 헤더 클래스
        ObjectMapper objectMapper = new ObjectMapper();
        String encodedAuth = new String(Base64.getEncoder().encode(("test_sk_4vZnjEJeQVxqjRyWaKOrPmOoBN0k" + ":").getBytes(StandardCharsets.UTF_8)));
        headers.setBasicAuth(encodedAuth); // Base64로 인코딩한 값을 헤더 설정 정보에 넣음
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Order order = orderService.findById(ProOrderId);
        order.connectPaymentMethod("Toss Payments");

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        return restTemplate.postForEntity("https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

    }
}
