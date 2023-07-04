package project.trendpick_pro.domain.rebate.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.rebate.repository.RebateOrderItemRepository;
import project.trendpick_pro.global.rsData.RsData;
import project.trendpick_pro.global.util.Ut;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RebateService {
    private final OrderService orderService;
    private final MemberService memberService;
    private final RebateOrderItemRepository rebateOrderItemRepository;

    @Transactional
    public RsData makeDate(String brandName,String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        // 데이터 가져오기
        List<OrderItem> orderItems = orderService.findAllByPayDateBetweenOrderByIdAsc(fromDate, toDate);

        if(orderItems.isEmpty()){
           return RsData.of("F-1","정산할 주문내역이 없습니다.");
        }
        List<OrderItem> brandOrderItems=new ArrayList<>();
                for(OrderItem item: orderItems) {
                    if (item.getProduct().getBrand().getName().equals(brandName)) {
                        brandOrderItems.add(item);
                    }
                }

        // 변환하기
        List<RebateOrderItem> rebateOrderItems = brandOrderItems
                .stream()
                .map(this::toRebateOrderItem)
                .toList();

        // 저장하기
        rebateOrderItems.forEach(item -> makeRebateOrderItem(brandName,item));
        return RsData.of("S-1", "정산데이터가 성공적으로 생성되었습니다.");
    }

    @Transactional
    public void makeRebateOrderItem(String brandName,RebateOrderItem item) {
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

    public RebateOrderItem toRebateOrderItem(OrderItem orderItem) {
        return new RebateOrderItem(orderItem);
    }

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

        if (rebateOrderItem.isRebateAvailable() == false) {
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