package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.orders.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
   Page<OrderItem> findAllByPayDateBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    List<OrderItem> findAllByPayDateBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
