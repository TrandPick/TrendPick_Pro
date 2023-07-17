package project.trendpick_pro.global.kafka.kafkasave.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OutboxMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "payload", nullable = false)
    private String payload;

    public OutboxMessage(String topic, String message, String payload) {
        this.topic = topic;
        this.message = message;
        this.payload = payload;
    }
}
