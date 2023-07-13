package project.trendpick_pro.global.kafka.view.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.global.kafka.view.entity.View;
import project.trendpick_pro.global.kafka.view.repository.ViewRepository;

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

    @Async
    public void requestIncrementViewCount(HttpSession session) {
        if (session.getAttribute("visited") == null) {
            kafkaTemplate.send("views", "increment", "1");
            session.setAttribute("visited", true);
        }
    }

    @Transactional
    public void incrementViewCount(String viewId) {
        viewRepository.findById(Long.valueOf(viewId)).get().increment();
    }

    @Transactional(readOnly = true)
    public Long getCount() {
        return viewRepository.findById(1L).get().getCount();
    }
}
