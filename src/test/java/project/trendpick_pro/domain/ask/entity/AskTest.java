package project.trendpick_pro.domain.ask.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.trendpick_pro.domain.answer.entity.Answer;

import static org.assertj.core.api.Assertions.assertThat;

class AskTest {

    @DisplayName("답변 생성지 답변 상태는 YET 이다.")
    @Test
    void initAsk() throws Exception {
        //given //when
        Ask ask = Ask.of("title", "content");

        //then
        assertThat(ask.getStatus()).isEqualTo(AskStatus.YET);
    }

    @DisplayName("답변이 완료되면 답변 상태는 COMPLETED 이다.")
    @Test
    void updateAskStatus() throws Exception {
        //given
        Ask ask = Ask.of("title", "content");

        //when
        Answer answer = Answer.write("content");
        answer.connectAsk(ask);

        //then
        assertThat(ask.getStatus()).isEqualTo(AskStatus.COMPLETED);
    }
}