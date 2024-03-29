package project.trendpick_pro.domain.ask.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import org.springframework.data.domain.Page;
import project.trendpick_pro.domain.ask.entity.Ask;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class AskResponse implements Serializable {

    private Long askId;
    private Long productId;
    private String productName;
    private String memberName;
    private Long memberId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdDate;

    @Builder
    @QueryProjection
    public AskResponse(Long askId, Long productId, String productName, String memberName, Long memberId, String title,
                       String content, String status, LocalDateTime createdDate) {
        this.askId = askId;
        this.productId = productId;
        this.productName = productName;
        this.memberName = memberName;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.createdDate = createdDate;
    }

    public static AskResponse of (Ask ask) {
        return AskResponse.builder()
                .askId(ask.getId())
                .memberName(ask.getAuthor().getUsername())
                .memberId(ask.getAuthor().getId())
                .productId(ask.getProduct().getId())
                .productName(ask.getProduct().getTitle())
                .title(ask.getTitle())
                .content(ask.getContent())
                .status(ask.getStatus().getValue())
                .createdDate(ask.getCreatedDate())
                .build();
    }

    public static Page<AskResponse> of(Page<Ask> asks){
        return asks.map(AskResponse::of);
    }
}