package project.trendpick_pro.domain.rebate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import project.trendpick_pro.domain.rebate.entity.MonthRebateData;

import java.util.Optional;

public interface MonthRebateDataRepository extends JpaRepository<MonthRebateData, Long> {
    @Query("select d from MonthRebateData d where d.store.brand = :storeName and d.yearMonth = :yearMonth")
    Optional<MonthRebateData> findByStoreNameAndYearMonth(
            @Param("storeName") String storeName, @Param("yearMonth") String yearMonth);

}
