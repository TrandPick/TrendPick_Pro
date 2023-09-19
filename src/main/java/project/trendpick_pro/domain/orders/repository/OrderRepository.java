package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.orders.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("SELECT o FROM Order o " +
            "WHERE o.paymentKey IS NULL " +
            "AND o.createdDate < :date")
    List<Order> findAllByPaymentKeyIsNullAndCreatedDateIsBefore(@Param("date") LocalDateTime date);
}
