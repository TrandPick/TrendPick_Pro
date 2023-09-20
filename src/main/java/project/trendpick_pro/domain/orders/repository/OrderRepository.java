package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.orders.entity.Order;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("select o from Order o " +
            "where o.paymentKey IS NULL " +
            "and o.createdDate <= :expireDate")
    Page<Order> findAllByPaymentKeyIsNullAndCreatedDateIsBefore(@Param("expireDate") LocalDateTime expireDate, Pageable pageable);
}
