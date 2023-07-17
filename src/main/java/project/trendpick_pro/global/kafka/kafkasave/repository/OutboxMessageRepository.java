package project.trendpick_pro.global.kafka.kafkasave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.global.kafka.kafkasave.entity.OutboxMessage;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, Long> {
}
