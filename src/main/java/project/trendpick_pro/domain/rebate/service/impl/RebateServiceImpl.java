package project.trendpick_pro.domain.rebate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.service.CashService;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.rebate.repository.RebateOrderItemRepository;
import project.trendpick_pro.domain.rebate.service.RebateService;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.global.util.rsData.RsData;
import project.trendpick_pro.global.util.Ut;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RebateServiceImpl implements RebateService {
    private final RebateOrderItemRepository rebateOrderItemRepository;
    private final OrderService orderService;
    private final CashService cashService;
    private final StoreService storeService;

    @Transactional
    @Override
    public RsData makeData(String brandName, String yearMonth) {
        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(Ut.date.getEndDayOf(yearMonth));
        List<OrderItem> orderItems = orderService.findAllByCreatedDateBetweenOrderByIdAsc(
                Ut.date.parse(fromDateStr), Ut.date.parse(toDateStr)
        );

        if(orderItems.isEmpty())
            return RsData.of("F-1","정산할 주문내역이 없습니다.");

        List<OrderItem> brandOrderItems=new ArrayList<>();
        for(OrderItem item: orderItems) {
            if (item.getProduct().getProductOption().getBrand().getName().equals(brandName)) {
                brandOrderItems.add(item);
            }
        }

        List<RebateOrderItem> rebateOrderItems = brandOrderItems
                .stream()
                .map(this::toRebateOrderItem)
                .toList();

        rebateOrderItems.forEach(this::updateOrCreateRebateData);
        return RsData.of("S-1", "정산데이터가 성공적으로 생성되었습니다.");
    }

    @Transactional
    @Override
    public RsData rebate(Long orderItemId) {
        RebateOrderItem rebateOrderItem = rebateOrderItemRepository.findByOrderItemId(orderItemId).orElse(null);

        RsData<Object> validateResult = validateAvailableRebate(rebateOrderItem);
        if (validateResult.isFail()) return validateResult;

        //캐시로그 생성
        CashLog cashLog = cashService.addCashLog(rebateOrderItem);
        //스토어 정산캐시 추가
        storeService.addRebateCash(rebateOrderItem.getSellerName(), rebateOrderItem.calculateRebatePrice());
        //정산완료
        rebateOrderItem.setRebateDone(cashLog);
        return RsData.of(
                "S-1",
                "주문품목번호 %d번에 대해서 정산을 완료하였습니다.".formatted(rebateOrderItem.getOrderItem().getId())
        );
    }

    @Override
    public List<RebateOrderItem> findRebateDataByCurrentYearMonth(String brandName,String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);
        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr); //yyyy-MM-dd HH:mm:ss.SSSSSS 패턴으로 만들기
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        return rebateOrderItemRepository.findAllByCreatedDateBetweenAndSellerNameOrderByIdAsc(fromDate, toDate, brandName);
    }


    private static RsData<Object> validateAvailableRebate(RebateOrderItem rebateOrderItem) {
        if(rebateOrderItem == null)
            return RsData.of("F-2", "존재하지 않는 정산 데이터입니다.");
        if (!rebateOrderItem.checkAlreadyRebate())
            return RsData.of("F-1", "이미 정산된 데이터입니다.");
        if(!rebateOrderItem.getOrder().isCompletedPurchaseDecision())
            return RsData.of("F-3", "구매결정이 완료되지 않은 데이터입니다.");
        return RsData.success();
    }

    private void updateOrCreateRebateData(RebateOrderItem item) {
        //orderItem의 id값으로 조회해봐서 이미 정산데이터로 생성되어 있다면, 업데이트 처리
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

    private RebateOrderItem toRebateOrderItem(OrderItem orderItem) {
        return new RebateOrderItem(orderItem);
    }

}