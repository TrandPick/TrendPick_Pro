package project.trendpick_pro.domain.rebate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RebateOrderItemRepository extends JpaRepository<RebateOrderItem, Long> {
    Optional<RebateOrderItem> findByOrderItemId(long orderItemId);
    List<RebateOrderItem> findAllByCreatedDateBetweenAndSellerNameOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate, String sellerName);
}