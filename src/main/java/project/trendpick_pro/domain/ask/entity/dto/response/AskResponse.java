package project.trendpick_pro.domain.ask.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.ask.entity.Ask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AskResponse {

    private Long id;
    private String author; //Member
    private String brand; //Brand

    private String title;
    private String content;
    private List<Answer> answerList = new ArrayList<>();
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;


    @Builder
    @QueryProjection
    public AskResponse(Long id, String author, String brand, String title, String content, List<Answer> answerList, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.author = author;
        this.brand = brand;
        this.title = title;
        this.content = content;
        this.answerList = answerList;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static AskResponse of (Ask ask) {
        return AskResponse.builder()
                .id(ask.getId())
                .author(ask.getAuthor())
                .brand(ask.getBrand())
                .title(ask.getTitle())
                .content(ask.getContent())
                .answerList(ask.getAnswerList())
                .createdDate(ask.getCreatedDate())
                .modifiedDate(ask.getModifiedDate())
                .build();
    }

}
