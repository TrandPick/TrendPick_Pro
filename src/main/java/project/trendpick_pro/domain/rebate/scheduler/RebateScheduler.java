package project.trendpick_pro.domain.rebate.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import project.trendpick_pro.domain.rebate.entity.MonthRebateData;
import project.trendpick_pro.domain.rebate.repository.MonthRebateDataRepository;
import project.trendpick_pro.domain.rebate.service.RebateService;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;
import project.trendpick_pro.domain.store.service.StoreService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class RebateScheduler {
    private final RebateService rebateService;
    private final MonthRebateDataRepository monthRebateDataRepository;
    private final StoreRepository storeRepository;
    private final StoreService storeService;

    @Scheduled(cron = "0 0 1 * * ?") //매월 1일 객체생성
    private void createMonthRebateObject(){
        String currentYearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        createEmptyRebateDataForAllStore(currentYearMonth);
    }

    @Scheduled(cron = "0 0 10 * * ?") //매월 10일 (배송확정 되는데까지 시간 고려)
    private void rebateFinish(){ //전체정산 (auto)
        String pastYearMonth = LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM")); //지난 달
        rebateForAllStore(pastYearMonth);
    }


    private void rebateForAllStore(String yearMonth) {
        List<Store> stores = storeRepository.findAll();
        for (Store store : stores) {
            MonthRebateData monthRebateData = rebateProcess(yearMonth, store);
            refund(monthRebateData);
        }
    }

    private void refund(MonthRebateData monthRebateData) {
        storeService.addRebateCash(monthRebateData.getStore(), monthRebateData.getNetProfit()); //캐시로 반환
        monthRebateData.refundDone();
    }

    private MonthRebateData rebateProcess(String yearMonth, Store store) {
        rebateService.makeRebateOrderItem(store.getBrand(), yearMonth);
        rebateService.rebate(store.getBrand(), yearMonth);
        return rebateService.findMonthRebateData(store.getBrand(), yearMonth);
    }

    private void createEmptyRebateDataForAllStore(String currentYearMonth) {
        List<MonthRebateData> data = new ArrayList<>();
        List<Store> stores = storeRepository.findAll();
        for (Store store : stores)
            data.add(new MonthRebateData(store, currentYearMonth));
        monthRebateDataRepository.saveAll(data);
    }
}
