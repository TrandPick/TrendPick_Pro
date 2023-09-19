package project.trendpick_pro.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.redis.redisson.service.RedissonService;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RedissonService redissonService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "orders", groupId = "order")
    public void orderToOrder(ConsumerRecord<String, String> message, Acknowledgment ack) {
        try {
            redissonService.processOrderWithLock(message.key(), message.value());
        } catch (Exception e) {
            log.error("orderToOrder process error : {} ", e.getMessage());
        } finally {
            ack.acknowledge();
        }
    }

    @KafkaListener(topicPattern = "standByOrder", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void message(@Payload String json) throws JsonProcessingException {
        OrderStateResponse response = objectMapper.readValue(json, OrderStateResponse.class);
        messagingTemplate.convertAndSendToUser(response.getEmail(), "/topic/standByOrder", json);
    }
}
