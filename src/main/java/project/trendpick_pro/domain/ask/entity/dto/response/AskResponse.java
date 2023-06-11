package project.trendpick_pro.domain.ask.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import org.springframework.data.domain.Page;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.answer.entity.dto.response.AnswerResponse;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AskResponse {

    private Long askId;
    private Long productId;
    private String memberName;
    private Long memberId;
    private String title;
    private String status;
    private LocalDateTime createdDate;

    @QueryProjection
    @Builder
    public AskResponse(Long askId, Long productId, String memberName, Long memberId, String title, String status, LocalDateTime createdDate) {
        this.askId = askId;
        this.productId = productId;
        this.memberName = memberName;
        this.memberId = memberId;
        this.title = title;
        this.status = status;
        this.createdDate = createdDate;
    }

    public static AskResponse of (Ask ask) {
        return AskResponse.builder()
                .askId(ask.getId())
                .memberName(ask.getAuthor().getUsername())
                .memberId(ask.getAuthor().getId())
                .productId(ask.getProduct().getId())
                .title(ask.getTitle())
                .status(ask.getStatus().getValue())
                .createdDate(ask.getCreatedDate())
                .build();
    }

    public static Page<AskResponse> of(Page<Ask> asks){
        return asks.map(ask -> AskResponse.of(ask));
    }

}
