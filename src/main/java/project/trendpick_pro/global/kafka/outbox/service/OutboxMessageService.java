package project.trendpick_pro.global.kafka.outbox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.global.kafka.outbox.entity.OrderMaterial;
import project.trendpick_pro.global.kafka.outbox.entity.OutboxMessage;
import project.trendpick_pro.global.kafka.outbox.repository.OutboxMessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboxMessageService {

    private final OutboxMessageRepository outboxMessageRepository;

    public OutboxMessage findById(Long id) {
        return outboxMessageRepository.findById(id).get();
    }

    public List<OutboxMessage> findAll() {
        return outboxMessageRepository.findAll();
    }

    @Transactional
    public void save(OutboxMessage outboxMessage) {
        outboxMessageRepository.save(outboxMessage);
    }

    @Transactional
    public void delete(OutboxMessage outboxMessage) {
        outboxMessageRepository.delete(outboxMessage);
    }

    @Transactional
    public void deleteAllById(List<Long> completedMessages) {
        outboxMessageRepository.deleteAllById(completedMessages);
    }

    public List<OrderMaterial> findOrderMaterial(String code){
        return outboxMessageRepository.findAllOrderMaterialByCode(code);
    }

    //주문 객쳉 생성 메시지 발행
    @Transactional
    public void publishOrderCreationMessage(String topic, String key, List<OrderMaterial> orderMaterials, String code) {
        outboxMessageRepository.save(new OutboxMessage(topic, key, key, orderMaterials, code));
    }

    @Transactional
    //주문 처리 메시지 발행
    public void publishOrderProcessMessage(String topic, String message, String payload, String email) {
        outboxMessageRepository.save(new OutboxMessage(topic, message, payload, email));
    }
}
