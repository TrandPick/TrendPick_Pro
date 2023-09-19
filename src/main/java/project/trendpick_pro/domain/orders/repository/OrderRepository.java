package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.orders.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("select o from Order o " +
            "where o.paymentKey IS NULL " +
            "and o.createdDate <= :expireDate")
    List<Order> findAllByPaymentKeyIsNullAndCreatedDateIsBefore(@Param("expireDate") LocalDateTime expireDate, Pageable pageable);
}
