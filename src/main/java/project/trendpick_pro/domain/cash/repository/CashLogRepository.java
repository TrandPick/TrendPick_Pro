package project.trendpick_pro.domain.cash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.cash.entity.CashLog;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}
