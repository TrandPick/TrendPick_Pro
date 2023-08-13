package project.trendpick_pro.domain.rebate.service;

import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

public interface RebateService {
    RsData makeDate(String brandName,String yearMonth);

    void makeRebateOrderItem(RebateOrderItem item);

    RebateOrderItem toRebateOrderItem(OrderItem orderItem);

    List<RebateOrderItem> findRebateOrderItemsByCreatedDateIn(String brandName,String yearMonth);
    RsData rebate(long orderItemId);
}