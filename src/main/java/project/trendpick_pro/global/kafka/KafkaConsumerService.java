package project.trendpick_pro.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import project.trendpick_pro.global.kafka.view.service.ViewService;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;
import project.trendpick_pro.domain.orders.service.OrderService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final OrderService orderService;
    private final ViewService viewService;

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topicPattern = "orders", groupId = "group_id")
    public void orderToOrder(@Payload String Id) throws JsonProcessingException {
        orderService.tryOrder(Id);
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
