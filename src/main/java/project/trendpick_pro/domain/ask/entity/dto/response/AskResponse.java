package project.trendpick_pro.domain.ask.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.ask.entity.Ask;

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


    @Builder
    @QueryProjection
    public AskResponse(Long id, String author, String brand, String title, String content, List<Answer> answerList) {
        this.id = id;
        this.author = author;
        this.brand = brand;
        this.title = title;
        this.content = content;
        this.answerList = answerList;
    }

    public static AskResponse of (Ask ask) {
        return AskResponse.builder()
                .id(ask.getId())
                .author(ask.getAuthor())
                .brand(ask.getBrand())
                .title(ask.getTitle())
                .content(ask.getContent())
                .answerList(ask.getAnswerList())
                .build();
    }
}
