package project.trendpick_pro.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;
import project.trendpick_pro.global.kafka.outbox.entity.OutboxMessage;
import project.trendpick_pro.global.kafka.outbox.service.OutboxMessageService;

import java.util.ArrayList;
import java.util.List;
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
    @Scheduled(fixedRate = 3000) //3초마다 아웃박스를 조회하며 메시지를 보낸다.
    public void sendMessage() throws JsonProcessingException {
        List<OutboxMessage> outboxMessages = outboxMessageService.findAll();

        List<Long> completedMessages = new ArrayList<>();
        for (OutboxMessage outboxMessage : outboxMessages) {
            if(outboxMessage.getTopic().equals("standByOrder")) //주문 처리 성공 메시지
                sendOrderProcessSuccessMessage(completedMessages, outboxMessage);

            else if(outboxMessage.getTopic().equals("orders"))
                sendCompleteOrderCreationMessage(completedMessages, outboxMessage); //주문 객체 생성 완료 메시지
        }

        if(!completedMessages.isEmpty())
            outboxMessageService.deleteAllById(completedMessages);
    }

    private void sendCompleteOrderCreationMessage(List<Long> completedMessages, OutboxMessage outboxMessage) {
        retryTemplate.execute(context -> {
        kafkaTemplate.send(outboxMessage.getTopic(), outboxMessage.getPayload(), outboxMessage.getCode());
        outboxMessage.disconnectOrderMaterial();
        completedMessages.add(outboxMessage.getId());
        log.info("Message sent: {}", outboxMessage.getMessage());
        return null;
    }, context -> {
        log.error("Failed to send message: {}", context.getLastThrowable().getMessage());
        return null;
    });
    }

    @Transactional
    public void sendOrderProcessFailMessage(Long orderId, String message, String email) throws JsonProcessingException {
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

    @Transactional
    public void sendOrderProcessSuccessMessage(List<Long> completedMessages, OutboxMessage outboxMessage) throws JsonProcessingException {
        OrderStateResponse response = OrderStateResponse.builder()
                .orderId(Long.valueOf(outboxMessage.getPayload()))
                .message(outboxMessage.getMessage())
                .email( outboxMessage.getEmail())
                .build();
        String json = objectMapper.writeValueAsString(response);
        retryTemplate.execute(context -> {
            kafkaTemplate.send("standByOrder", UUID.randomUUID().toString(), json);
            completedMessages.add(outboxMessage.getId());
            log.info("Message sent: {}", outboxMessage.getMessage());
            return null;
        }, context -> {
            log.error("Failed to send message: {}", context.getLastThrowable().getMessage());
            return null;
        });
    }


}
