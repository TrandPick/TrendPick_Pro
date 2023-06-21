package project.trendpick_pro.global.tosspayment.dto;

import lombok.Data;

@Data
public class PaymentCardResponse {
    private String company;
    private String number;
    private String installmentPlanMonths;
    private String isInterestFree;
    private String approveNo;
    private String useCardPoint;
    private String cardType;
    private String ownerType;
    private String acquireStatus;
    private String receiptUrl;
}