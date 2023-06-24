package project.trendpick_pro.domain.coupon.entity.expirationPeriod;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpirationPeriod {
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;

    //만료기간은 직접 시작일과 마감일을 설정하는 경우
    // 발급날로부터 oo일까지로 설정하는 경우 2가지
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpirationType expirationType;

    @Column(name = "issue_after_date")
    private Integer issueAfterDate;

    //시작일은 해당 쿠폰이 발급된 날로
    private ExpirationPeriod(ExpirationType expirationType, Integer issueAfterDate, LocalDateTime startDate, LocalDateTime endDate) {
        this.expirationType = expirationType;
        this.issueAfterDate = issueAfterDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //스토어쿠폰이 만들어질때
    public static ExpirationPeriod assignIssueAfterDate(Integer issueAfterDate) {
        return new ExpirationPeriod(ExpirationType.ISSUE_AFTER_DATE, issueAfterDate, null, null);
    }

    //스토어쿠폰이 만들어질때
    public static ExpirationPeriod assignPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return new ExpirationPeriod(ExpirationType.PERIOD, null, startDate, endDate);
    }

    //만들어진 쿠폰을 회원이 발급받을때
    public ExpirationPeriod CopyExpirationPeriod() {
        if (this.expirationType == ExpirationType.PERIOD)
            return new ExpirationPeriod(ExpirationType.PERIOD, null, this.getStartDate(), this.getEndDate());
        return new ExpirationPeriod(ExpirationType.ISSUE_AFTER_DATE, null, LocalDateTime.now(), LocalDateTime.now().plusDays(this.getIssueAfterDate()));
    }


}
