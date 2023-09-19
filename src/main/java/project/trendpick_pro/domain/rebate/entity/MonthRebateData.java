package project.trendpick_pro.domain.rebate.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.global.util.Ut;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MonthRebateData extends BaseTimeEntity { //월 정산데이터

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "month_rebate_data_id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "'year_month'")
    private String yearMonth; //연월
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    @Column(name = "total_sales")
    private long totalSales; //총 매출
    @Column(name = "net_profit")
    private long netProfit; //순이익 : 총 매출 - 수수료
    @Column(name = "tax")
    private long tax; //수수료
    @Column(name = "sale_count")
    private int saleCount;  //총판매개수
    @Column(name = "total_discount")
    private int totalDiscount; //총할인금액
    @Column(name = "refund_date")
    private LocalDateTime refundDate; //환급날짜
    @Column(name = "refund_amount")
    private Long refundAmount;

    public MonthRebateData(Store store, String yearMonth){
        this.store = store;
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);
        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        this.yearMonth = yearMonth;
        this.fromDate = Ut.date.parse(fromDateStr); //yyyy-MM-dd HH:mm:ss.SSSSSS 패턴으로 만들기
        this.toDate = Ut.date.parse(toDateStr);
        this.totalSales = 0;
        this.netProfit = 0;
        this.tax = 0;
        this.saleCount = 0;
        this.totalDiscount = 0;
    }

    public void rebate(List<RebateOrderItem> rebateOrderItems){ //총정산
        for (RebateOrderItem rebateOrderItem : rebateOrderItems) {
            rebate(rebateOrderItem);
        }
    }

    public void rebate(RebateOrderItem rebateOrderItem){ //총정산
            this.totalSales += rebateOrderItem.getTotalPrice();
            this.netProfit += rebateOrderItem.calculateRebatePrice();
            this.saleCount += rebateOrderItem.getQuantity();
            this.tax += (int)(rebateOrderItem.getTotalPrice() * 0.05);
            this.totalDiscount += rebateOrderItem.getDiscountPrice();
    }

    //캐시로 환급
    public void refundDone(){
        this.refundDate = LocalDateTime.now();
        this.refundAmount = getNetProfit();
    }
}
