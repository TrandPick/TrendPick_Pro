package project.trendpick_pro.global.tosspayment.service;

import project.trendpick_pro.global.tosspayment.dto.PaymentResultResponse;

public interface PaymentService {
    PaymentResultResponse requestPayment(String paymentKey, String orderId, Integer amount);
    void cancelPayment(String paymentKey);
}