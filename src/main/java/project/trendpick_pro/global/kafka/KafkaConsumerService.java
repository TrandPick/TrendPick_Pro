package project.trendpick_pro.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.common.redisson.service.RedissonService;
import project.trendpick_pro.global.kafka.kafkasave.entity.OutboxMessage;
import project.trendpick_pro.global.kafka.kafkasave.service.OutboxMessageService;
import project.trendpick_pro.global.kafka.view.service.ViewService;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.redis.exception.RedisLockException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
