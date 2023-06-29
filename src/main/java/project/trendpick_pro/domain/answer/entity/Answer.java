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
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
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

    @ManyToOne
    @JoinColumn(name = "ask_id")
    private Ask ask;

    private String content;

    public static Answer write(AnswerForm answerForm) {
        Answer answer = Answer
                .builder()
                .content(answerForm.getContent())
                .build()
                ;

        return answer;
    }

    public void connectAsk(Ask ask){
        ask.getAnswerList().add(this);
        ask.changeStatus();
        this.ask = ask;
    }

    public void update(AnswerForm answerForm) {
        this.content = answerForm.getContent();
    }
}
