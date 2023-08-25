package project.trendpick_pro.domain.rebate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.service.CashService;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.rebate.entity.MonthRebateData;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.rebate.repository.MonthRebateDataRepository;
import project.trendpick_pro.domain.rebate.repository.RebateOrderItemRepository;
import project.trendpick_pro.domain.rebate.service.RebateService;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.global.util.rsData.RsData;
import project.trendpick_pro.global.util.Ut;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RebateServiceImpl implements RebateService {
    private final RebateOrderItemRepository rebateOrderItemRepository;
    private final MonthRebateDataRepository monthRebateDataRepository;
    private final OrderService orderService;
    private final CashService cashService;
    private final StoreService storeService;

    @Transactional
    @Override
    public RsData makeRebateOrderItem(String brandName, String yearMonth) {
        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(Ut.date.getEndDayOf(yearMonth));
        List<OrderItem> orderItems = orderService.findAllByCreatedDateBetweenOrderByIdAsc(
                Ut.date.parse(fromDateStr), Ut.date.parse(toDateStr)
        );

        if(orderItems.isEmpty())
            return RsData.of("F-1","정산할 주문내역이 없습니다.");

        List<RebateOrderItem> rebateOrderItems = convertToRebateOrderItem(brandName, orderItems);

        rebateOrderItems.forEach(this::updateOrCreateRebateOrderItem);
        return RsData.of("S-1", "정산 데이터가 성공적으로 생성되었습니다.");
    }
    

    @Transactional
    @Override
    public RsData rebate(String storeName, Long orderItemId) {
        RebateOrderItem rebateOrderItem = rebateOrderItemRepository.findByOrderItemId(orderItemId).orElse(null);

        RsData<Object> validateResult = validateAvailableRebate(rebateOrderItem);
        if (validateResult.isFail()) return validateResult;

        //캐시로그 생성
        CashLog cashLog = cashService.addCashLog(rebateOrderItem);

        //월정산
        String yearMonth = rebateOrderItem.getOrderItemCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        MonthRebateData monthRebateData = findMonthRebateData(storeName, yearMonth);
        monthRebateData.rebate(rebateOrderItem);

        //정산완료
        rebateOrderItem.setRebateDone(cashLog);
        return RsData.of(
                "S-1",
                "주문품목번호 %d번에 대해서 정산을 완료하였습니다.".formatted(rebateOrderItem.getOrderItem().getId())
        );
    }

    @Override
    @Transactional
    public RsData rebate(String storeName, String yearMonth) {
        List<RebateOrderItem> rebateOrderItems = findRebateOrderItemByCurrentYearMonth(storeName, yearMonth);
        int count = 0;
        for (RebateOrderItem rebateOrderItem : rebateOrderItems){
            if(rebate(storeName, rebateOrderItem.getOrderItem().getId()).isSuccess()) //정산 성공 개수 세기
                count++;
        }

        if(count == 0)
            return RsData.of("F-1", "정산 처리 가능한 데이터가 존재하지 않습니다.");

        return RsData.of("S-1", "%s건의 데이터를 정산처리 하였습니다.".formatted(count));
    }

    @Override
    @Transactional
    public MonthRebateData findMonthRebateData(String brandName, String yearMonth) {
        MonthRebateData monthRebateData = monthRebateDataRepository.findByStoreNameAndYearMonth(brandName, yearMonth).orElse(null);
        if(monthRebateData == null)
            return monthRebateDataRepository.save(new MonthRebateData(storeService.findByBrand(brandName), yearMonth));
        return monthRebateData;
    }

    @Override
    public List<RebateOrderItem> findRebateOrderItemByCurrentYearMonth(String brandName, String yearMonth) {
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
        if (rebateOrderItem.isAlreadyRebated())
            return RsData.of("F-1", "이미 정산된 데이터입니다.");
        if(!rebateOrderItem.getOrder().isCompletedPurchaseDecision())
            return RsData.of("F-3", "구매결정이 완료되지 않은 데이터입니다.");
        return RsData.success();
    }

    private Long updateOrCreateRebateOrderItem(RebateOrderItem item) {
        RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);
        if(oldRebateOrderItem == null) //없으면 생성
            return rebateOrderItemRepository.save(item).getId();

        if (oldRebateOrderItem.isRebateDone()) //이미 정산처리 되어있다면 return
            return oldRebateOrderItem.getId();

        return rebateOrderItemRepository.save(oldRebateOrderItem.updateWith(item)).getId(); //둘 다 아니라면 업데이트 후 리턴
    }
    private RebateOrderItem toRebateOrderItem(OrderItem orderItem) {
        return new RebateOrderItem(orderItem);
    }
    private  List<RebateOrderItem> convertToRebateOrderItem(String brandName, List<OrderItem> orderItems) {
        List<OrderItem> filteredOrderItem = new ArrayList<>();
        for(OrderItem item: orderItems) {
            if (item.getProduct().getProductOption().getBrand().getName().equals(brandName))
                filteredOrderItem.add(item);
        }
        
        return filteredOrderItem 
                .stream()
                .map(this::toRebateOrderItem)
                .toList();
    }
}