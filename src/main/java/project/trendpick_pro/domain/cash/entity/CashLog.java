package project.trendpick_pro.domain.cash.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import project.trendpick_pro.domain.member.entity.Member;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class CashLog {
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

    public CashLog(Long id){
        this.id=id;
    }
    public enum EvenType {
        충전__무통장입금,
        충전__토스페이먼츠,
        출금__통장입금,
        사용__토스페이먼츠_주문결제,
        사용__예치금_주문결제,
        환불__예치금_주문결제,
        브랜드정산__예치금;
    }
}