package project.trendpick_pro.global.tosspayment.service;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.trendpick_pro.global.tosspayment.dto.PaymentResultResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Service
public class PaymentService {

    @Value("${toss.secretKey}")
    private String secretKey;

    @Value("${toss.url}")
    private String tossURL;

    public PaymentResultResponse requestPayment(String paymentKey, String orderId, Integer amount) {
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
}