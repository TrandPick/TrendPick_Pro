package project.trendpick_pro.domain.answer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import project.trendpick_pro.domain.answer.entity.dto.request.AnswerRequest;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Answer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;
    private String author; //멤버 엔티티와 맵핑 해줘야 한다. (임시 String)

    @ManyToOne
    @JoinColumn(name = "ask_id")
    private Ask ask;

    private String content;

    public static Answer write(Ask ask, String member, AnswerRequest answerRequest) {
        Answer answer = Answer
                .builder()
                .author(member) //Member
                .ask(ask)
                .content(answerRequest.getContent())
                .build()
                ;

        ask.getAnswerList().add(answer); //양방향
        return answer;
    }

    public void update(AnswerRequest answerRequest) {
        this.content = content;
    }
}
