package project.trendpick_pro.domain.notification.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.answer.entity.dto.response.AnswerResponse;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.domain.product.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {
    private Long id;
    private LocalDateTime creatDate;
    private String title;
    private String content;

    @Builder
    @QueryProjection
    public NotificationResponse(Long id, LocalDateTime creatDate,String title, String content) {
        this.id = id;
        this.creatDate = creatDate;
        this.title = title;
        this.content = content;
    }
}
