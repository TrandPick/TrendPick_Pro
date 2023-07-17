package project.trendpick_pro.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.global.kafka.kafkasave.entity.OutboxMessage;
import project.trendpick_pro.global.kafka.kafkasave.service.OutboxMessageService;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final OutboxMessageService outboxMessageService;

    private final RetryTemplate retryTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void sendMessage(Long id) {
        OutboxMessage outboxMessage = outboxMessageService.findById(id);
        retryTemplate.execute(context -> {
            kafkaTemplate.send(outboxMessage.getTopic(), outboxMessage.getPayload(), outboxMessage.getMessage());
            outboxMessageService.delete(outboxMessage);
            return null;
        }, context -> {
            log.error("Failed to send message: {}", context.getLastThrowable().getMessage());
            return null;
        });
    }
}
