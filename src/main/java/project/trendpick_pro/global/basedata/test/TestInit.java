package project.trendpick_pro.global.basedata.test;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class TestInit {

    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 10000)
    public void applicationRunner() {
        messagingTemplate.convertAndSend("/topic/trendpick/orders/standByOrder", "test");
    }
}
