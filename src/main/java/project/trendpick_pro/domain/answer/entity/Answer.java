package project.trendpick_pro.domain.answer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import project.trendpick_pro.domain.ask.entity.Ask;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;
    private String author; //멤버 엔티티와 맵핑 해줘야 한다. (임시 String)
    @CreatedDate
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "ask_id")
    private Ask ask;

    private String content;
}
