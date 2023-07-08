package project.trendpick_pro.domain.withdraw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;


public interface WithdrawApplyRepository extends JpaRepository<WithdrawApply, Long> {
}
