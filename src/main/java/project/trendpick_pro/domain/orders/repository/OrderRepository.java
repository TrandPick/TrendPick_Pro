package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.user.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User member, Pageable pageable);
}
