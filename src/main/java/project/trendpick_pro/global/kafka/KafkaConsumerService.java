package project.trendpick_pro.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.global.kafka.kafkasave.entity.OutboxMessage;
import project.trendpick_pro.global.kafka.kafkasave.service.OutboxMessageService;
import project.trendpick_pro.global.kafka.view.service.ViewService;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;
import project.trendpick_pro.domain.orders.service.OrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final OrderService orderService;
    private final ViewService viewService;

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private final PriorityQueue<ConsumerRecord<String, String>> queue =
            new PriorityQueue<>(Comparator.comparing(ConsumerRecord::timestamp));

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @KafkaListener(topics = "orders", groupId = "group_id")
    public void orderToOrder(ConsumerRecord<String, String> message, Acknowledgment ack) {
        try {
            queue.add(message);
            while (!queue.isEmpty()) {
                ConsumerRecord<String, String> record = queue.poll();
                executorService.submit(() -> {
                    try {
                        log.info("record: {}, {}, {}", record.timestamp(), record.value(), record.key());
                        orderService.tryOrder(record.key());
                    } catch (Exception e) {
                        log.error("Error processing message", e);
                    }
                });
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing messages", e);
        }
    }

    @KafkaListener(topicPattern = "standByOrder", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void message(@Payload String json) throws JsonProcessingException {
        OrderStateResponse response = objectMapper.readValue(json, OrderStateResponse.class);
        messagingTemplate.convertAndSendToUser(response.getEmail(), "/topic/standByOrder", json);
    }

    @KafkaListener(topicPattern = "views", groupId = "group_id")
    public void handleIncrementViewCount(@Payload String viewId) {
        viewService.incrementViewCount(viewId);
    }
}
