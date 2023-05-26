package project.trendpick_pro.domain.ask.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import project.trendpick_pro.domain.answer.entity.Answer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Ask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ask_id")
    private Long id;
    private String author; //멤버 엔티티로 수정 필요
    private String brand; //브랜드 엔티티로 수정 필요
    @CreatedDate
    private LocalDateTime createDate;

    private String title;
    private String content;

    @OneToMany(mappedBy = "ask", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Answer> answerList = new ArrayList<>();
}
