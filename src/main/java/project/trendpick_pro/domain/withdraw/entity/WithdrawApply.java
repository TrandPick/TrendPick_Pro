package project.trendpick_pro.domain.withdraw.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.withdraw.entity.dto.WithDrawApplyForm;

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

    public boolean checkAlreadyProcessed() { //이미 처리되었는지 확인
        if (withdrawDate != null || withdrawCashLog != null || cancelDate != null) {
            return true;
        }

        return false;
    }

    public void setApplyDone(CashLog cashLog, String msg) {
        withdrawDate = LocalDateTime.now();
        this.withdrawCashLog = cashLog;
        this.msg = msg;

    }

    public void setCancelDone(String msg) {
        cancelDate = LocalDateTime.now();
        this.msg = msg;
    }

    static public WithdrawApply of(WithDrawApplyForm withDrawApplyForm, Member applicant){
        return WithdrawApply.builder()
                .bankName(withDrawApplyForm.getBankName())
                .bankAccountNo(withDrawApplyForm.getBankAccountNo())
                .price(withDrawApplyForm.getPrice())
                .applicant(applicant)
                .build();
    }

    public boolean isApplyDone() {
        return withdrawDate != null;
    }

    public boolean isCancelDone() {
        return cancelDate != null;
    }
}