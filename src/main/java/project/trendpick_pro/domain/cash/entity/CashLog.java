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
        출금__통장입금,
        브랜드정산__예치금;
    }
}