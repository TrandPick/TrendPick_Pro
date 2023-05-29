package project.trendpick_pro.domain.notification.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NotificationRequest {
    @NotBlank
    private Long memberId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;

}
