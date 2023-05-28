package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.orders.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
