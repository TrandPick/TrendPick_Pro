package project.trendpick_pro.global.kafka.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.global.kafka.outbox.entity.OrderMaterial;
import project.trendpick_pro.global.kafka.outbox.entity.OutboxMessage;

import java.util.List;
import java.util.Optional;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, Long> {

    @Query("select o from OrderMaterial o where o.code = :code")
    List<OrderMaterial> findAllOrderMaterialByCode(@Param("code") String code);
}
