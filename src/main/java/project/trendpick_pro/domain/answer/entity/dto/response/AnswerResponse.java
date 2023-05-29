package project.trendpick_pro.domain.answer.entity.dto.response;

import lombok.Getter;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.ask.entity.Ask;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerResponse {

    private Long id;
    private Ask ask;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    @QueryProjection
    public AnswerResponse(Long id, Ask ask, String content, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.ask = ask;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static AnswerResponse of (Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .ask(answer.getAsk())
                .content(answer.getContent())
                .createdDate(answer.getCreatedDate())
                .modifiedDate(answer.getModifiedDate())
                .build();
    }

}

