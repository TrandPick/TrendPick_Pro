package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderStatus;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    Page<Order> findAllByStatusAndCreatedDateIsBefore(OrderStatus status, LocalDateTime date, Pageable pageable);

}
