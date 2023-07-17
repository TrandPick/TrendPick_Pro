package project.trendpick_pro.global.kafka.kafkasave.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.global.kafka.kafkasave.entity.OutboxMessage;
import project.trendpick_pro.global.kafka.kafkasave.repository.OutboxMessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxMessageService {

    private final OutboxMessageRepository outboxMessageRepository;

    @Transactional
    public void save(OutboxMessage outboxMessage) {
        outboxMessageRepository.save(outboxMessage);
    }

    @Transactional
    public void delete(OutboxMessage outboxMessage) {
        outboxMessageRepository.delete(outboxMessage);
    }

    @Transactional(readOnly = true)
    public OutboxMessage findById(Long id) {
        return outboxMessageRepository.findById(id).get();
    }
}
