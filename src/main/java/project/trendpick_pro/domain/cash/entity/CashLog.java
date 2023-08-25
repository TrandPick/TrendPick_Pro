package project.trendpick_pro.domain.cash.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class CashLog extends BaseTimeEntity { //돈의 흐름을 기록하기 위한 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cash_log_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    private Member member;
    private String store;
    private long price; // 변동
    private Long rebateOrderItemId;
    private Long withDrawApplyId;

    @Enumerated(EnumType.STRING)
    private EvenType eventType;
    public enum EvenType {
        출금__통장입금,
        브랜드정산__예치금;
    }

    static public CashLog of(WithdrawApply withdrawApply){
        return CashLog.builder()
                .withDrawApplyId(withdrawApply.getId())
                .price(withdrawApply.getPrice() * -1)
                .store(withdrawApply.getStore())
                .eventType(EvenType.출금__통장입금)
                .build();
    }

    static public CashLog of(RebateOrderItem rebateOrderItem){
        return CashLog.builder()
                .rebateOrderItemId(rebateOrderItem.getId())
                .price(rebateOrderItem.calculateRebatePrice())
                .store(rebateOrderItem.getSellerName())
                .eventType(EvenType.브랜드정산__예치금)
                .build();
    }
}