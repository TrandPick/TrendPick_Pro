package project.trendpick_pro.domain.answer.entity.dto.response;

import jakarta.persistence.*;
import lombok.Getter;


import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.ask.entity.Ask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerResponse {

    private Long id;
    private String author; //Member //브렌드 매니저 권한을 가지고 있어야함.
    private Ask ask;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    @QueryProjection
    public AnswerResponse(Long id, String author, Ask ask, String content, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.author = author;
        this.ask = ask;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static AnswerResponse of (Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .author(answer.getAuthor())
                .ask(answer.getAsk())
                .content(answer.getContent())
                .createdDate(answer.getCreatedDate())
                .modifiedDate(answer.getModifiedDate())
                .build();
    }

}

