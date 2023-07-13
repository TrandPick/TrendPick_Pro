package project.trendpick_pro.domain.answer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.answer.entity.form.AnswerForm;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;

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
        return Answer.builder()
                .content(answerForm.getContent())
                .build();
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
