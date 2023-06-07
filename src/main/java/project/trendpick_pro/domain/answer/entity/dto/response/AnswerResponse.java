package project.trendpick_pro.domain.answer.entity.dto.response;

import lombok.*;
import com.querydsl.core.annotations.QueryProjection;
import org.springframework.data.domain.Page;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {

    private Long answerId;
    private Long askId;
    private String content;
    private LocalDateTime createdDate;

    public static AnswerResponse of (Answer answer) {
        return AnswerResponse.builder()
                .answerId(answer.getId())
                .askId(answer.getAsk().getId())
                .content(answer.getContent())
                .createdDate(answer.getCreatedDate())
                .build();
    }

    public static List<AnswerResponse> of(List<Answer> answers){
        List<AnswerResponse> answerResponseList = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerResponse answerResponse = AnswerResponse.of(answer);
            answerResponseList.add(answerResponse);
        }
        return answerResponseList;
    }
}

