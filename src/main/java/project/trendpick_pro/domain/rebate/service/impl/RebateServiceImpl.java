package project.trendpick_pro.domain.rebate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.rebate.repository.RebateOrderItemRepository;
import project.trendpick_pro.domain.rebate.service.RebateService;
import project.trendpick_pro.global.util.rsData.RsData;
import project.trendpick_pro.global.util.Ut;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RebateServiceImpl implements RebateService {
    private final OrderService orderService;
    private final MemberService memberService;
    private final RebateOrderItemRepository rebateOrderItemRepository;

    @Transactional
    public RsData makeDate(String brandName, String yearMonth) {
        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(Ut.date.getEndDayOf(yearMonth));
        List<OrderItem> orderItems = orderService.findAllByPayDateBetweenOrderByIdAsc(
                Ut.date.parse(fromDateStr), Ut.date.parse(toDateStr)
        );

        if(orderItems.isEmpty()){
            return RsData.of("F-1","정산할 주문내역이 없습니다.");
        }
        List<OrderItem> brandOrderItems=new ArrayList<>();
        for(OrderItem item: orderItems) {
            if (item.getProduct().getBrand().getName().equals(brandName)) {
                brandOrderItems.add(item);
            }
        }

        List<RebateOrderItem> rebateOrderItems = brandOrderItems
                .stream()
                .map(this::toRebateOrderItem)
                .toList();

        rebateOrderItems.forEach(this::makeRebateOrderItem);
        return RsData.of("S-1", "정산데이터가 성공적으로 생성되었습니다.");
    }

    @Transactional
    public void makeRebateOrderItem(RebateOrderItem item) {
        RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);
        if (oldRebateOrderItem != null) {
            if (oldRebateOrderItem.isRebateDone()) {
                return;
            }
            oldRebateOrderItem.updateWith(item);
            rebateOrderItemRepository.save(oldRebateOrderItem);
        } else {
            rebateOrderItemRepository.save(item);
        }
    }
    @Transactional
    public RebateOrderItem toRebateOrderItem(OrderItem orderItem) {
        return new RebateOrderItem(orderItem);
    }

    @Transactional(readOnly = true)
    public List<RebateOrderItem> findRebateOrderItemsByPayDateIn(String brandName,String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        return rebateOrderItemRepository.findAllByPayDateBetweenAndSellerNameOrderByIdAsc(fromDate, toDate,brandName);
    }

    @Transactional
    public RsData rebate(long orderItemId) {
        RebateOrderItem rebateOrderItem = rebateOrderItemRepository.findByOrderItemId(orderItemId).get();

        if (!rebateOrderItem.isRebateAvailable()) {
            return RsData.of("F-1", "정산을 할 수 없는 상태입니다.");
        }

        int calculateRebatePrice = rebateOrderItem.calculateRebatePrice();

        CashLog cashLog = memberService.addCash(
                rebateOrderItem.getSellerName(),
                calculateRebatePrice,
                rebateOrderItem.getSeller(),
                CashLog.EvenType.브랜드정산__예치금
        ).getData().getCashLog();


        rebateOrderItem.setRebateDone(cashLog.getId());
        return RsData.of(
                "S-1",
                "주문품목번호 %d번에 대해서 정산을 완료하였습니다.".formatted(rebateOrderItem.getOrderItem().getId())
        );
    }
}