package project.trendpick_pro.domain.withdraw.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.member.entity.Member;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class WithdrawApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "with_draw_apply_id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    private Member applicant;
    private String bankName;
    private String bankAccountNo;
    private int price;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private CashLog withdrawCashLog; // 출금에 관련된 내역
    private LocalDateTime withdrawDate;
    private LocalDateTime cancelDate;
    private String msg;

    public boolean isApplyDoneAvailable() {
        if (withdrawDate != null || withdrawCashLog != null || cancelDate != null) {
            return false;
        }

        return true;
    }

    public void setApplyDone(Long cashLogId, String msg) {
        withdrawDate = LocalDateTime.now();
        this.withdrawCashLog = new CashLog(cashLogId);
        this.msg = msg;
    }

    public void setCancelDone(String msg) {
        cancelDate = LocalDateTime.now();
        this.msg = msg;
    }

    public boolean isCancelAvailable() {
        return isApplyDoneAvailable();
    }

    public boolean isApplyDone() {
        return withdrawDate != null;
    }

    public boolean isCancelDone() {
        return cancelDate != null;
    }
}