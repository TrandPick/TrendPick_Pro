package project.trendpick_pro.domain.rebate.service;

import project.trendpick_pro.domain.rebate.entity.MonthRebateData;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

public interface RebateService {
    RsData makeRebateOrderItem(String brandName, String yearMonth);
    List<RebateOrderItem> findRebateOrderItemByCurrentYearMonth(String brandName, String yearMonth);
    RsData rebate(String storeName, Long orderItemId);
    RsData rebate(String brandName, String currentYearMonth);
    MonthRebateData findMonthRebateData(String brandName, String yearMonth);
}