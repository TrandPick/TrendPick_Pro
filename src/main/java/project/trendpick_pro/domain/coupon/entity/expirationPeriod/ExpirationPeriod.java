package project.trendpick_pro.domain.coupon.entity.expirationPeriod;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ExpirationPeriod {

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpirationType expirationType;

    private Integer issueAfterDate;

    public void updateDate(LocalDateTime startDate) {
        this.startDate = startDate;
        this.endDate = startDate.plusDays(issueAfterDate);
    }

    @Builder
    private ExpirationPeriod(ExpirationType expirationType, Integer issueAfterDate, LocalDateTime startDate, LocalDateTime endDate) {
        this.expirationType = expirationType;
        this.issueAfterDate = issueAfterDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static ExpirationPeriod assignIssueAfterDate(Integer issueAfterDate) {
        return ExpirationPeriod.builder()
                .expirationType(ExpirationType.ISSUE_AFTER_DATE)
                .issueAfterDate(issueAfterDate)
                .build();
    }

    public static ExpirationPeriod assignPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return ExpirationPeriod.builder()
                .expirationType(ExpirationType.PERIOD)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
