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
import project.trendpick_pro.global.redis.redisson.service.RedissonService;
import project.trendpick_pro.global.kafka.view.service.ViewService;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RedissonService redissonService;
    private final ViewService viewService;

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "orders", groupId = "order")
    public void orderToOrder(ConsumerRecord<String, String> message, Acknowledgment ack) {
        try {
            redissonService.processOrderWithLock(message.key());
            ack.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topicPattern = "standByOrder", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void message(@Payload String json) throws JsonProcessingException {
        OrderStateResponse response = objectMapper.readValue(json, OrderStateResponse.class);
        messagingTemplate.convertAndSendToUser(response.getEmail(), "/topic/standByOrder", json);
    }

    @KafkaListener(topicPattern = "views", groupId = "view")
    public void handleIncrementViewCount(@Payload String viewId) {
        viewService.incrementViewCount(viewId);
    }
}
