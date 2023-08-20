package project.trendpick_pro.domain.cash.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class CashLog { //돈의 흐름을 기록하기 위한 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cash_log_id")
    private Long id;

    private String relTypeCode;

    private Long relId;

    @ManyToOne(fetch = LAZY)
    private Member member;

    private long price; // 변동

    @Enumerated(EnumType.STRING)
    private EvenType eventType;
    public enum EvenType {
        출금__통장입금,
        브랜드정산__예치금;
    }

    static public CashLog of(WithdrawApply withdrawApply){
        return CashLog.builder()
                .price(withdrawApply.getPrice() * -1)
                .relTypeCode(withdrawApply.getBankName())
                .eventType(EvenType.출금__통장입금)
                .build();
    }

    static public CashLog of(RebateOrderItem rebateData){
        return CashLog.builder()
                .price(rebateData.calculateRebatePrice())
                .relTypeCode(rebateData.getSellerName())
                .eventType(EvenType.브랜드정산__예치금)
                .build();
    }
}