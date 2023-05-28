package project.trendpick_pro.domain.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.delivery.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
