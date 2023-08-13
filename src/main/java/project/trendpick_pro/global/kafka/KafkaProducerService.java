package project.trendpick_pro.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;
import project.trendpick_pro.global.kafka.kafkasave.entity.OutboxMessage;
import project.trendpick_pro.global.kafka.kafkasave.service.OutboxMessageService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final OutboxMessageService outboxMessageService;

    private final RetryTemplate retryTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Transactional
    public void sendMessage(Long id) {
        OutboxMessage outboxMessage = outboxMessageService.findById(id);
        retryTemplate.execute(context -> {
            kafkaTemplate.send(outboxMessage.getTopic(), outboxMessage.getPayload(), outboxMessage.getMessage());
            log.info("Message sent: {}", outboxMessage.getMessage());
            outboxMessageService.delete(outboxMessage);
            return null;
        }, context -> {
            log.error("Failed to send message: {}", context.getLastThrowable().getMessage());
            return null;
        });
    }

    @Transactional
    public void sendMessage(Long orderId, String message, String email) throws JsonProcessingException {
        OrderStateResponse response = OrderStateResponse.builder()
                .orderId(orderId)
                .message(message)
                .email(email)
                .build();
        String json = objectMapper.writeValueAsString(response);
        retryTemplate.execute(context -> {
            kafkaTemplate.send("standByOrder", UUID.randomUUID().toString(), json);
            log.info("Message sent: {}", message);
            return null;
        }, context -> {
            log.error("Failed to send message: {}", context.getLastThrowable().getMessage());
            return null;
        });
    }
}
