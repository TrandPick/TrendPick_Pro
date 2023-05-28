package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.orders.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
