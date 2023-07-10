package project.trendpick_pro.domain.common.view.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.common.view.entity.View;
import project.trendpick_pro.domain.common.view.repository.ViewRepository;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final ViewRepository viewRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void registerView() {
        viewRepository.save(new View());
    }

    @Transactional(readOnly = true)
    public int findSize() {
        return viewRepository.findAll().size();
    }

    public void requestIncrementViewCount(HttpSession session) {
        if (session.getAttribute("visited") == null) {
            kafkaTemplate.send("views", "increment", "1");
            session.setAttribute("visited", true);
        }
    }

    @Transactional
    @KafkaListener(topicPattern = "views", groupId = "group_id")
    public void handleIncrementViewCount(@Payload String viewId) {
        View totalView = viewRepository.findById(Long.valueOf(viewId)).get();
        totalView.increment();
    }

    @Transactional(readOnly = true)
    public Long getCount() {
        return viewRepository.findById(1L).get().getCount();
    }
}
