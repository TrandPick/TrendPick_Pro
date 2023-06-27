package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.orders.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
