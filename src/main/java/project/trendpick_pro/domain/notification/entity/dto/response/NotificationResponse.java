package project.trendpick_pro.domain.notification.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.notification.entity.Notification;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationResponse {
    private Long id;

    private String orderState;

    private String deliveryState;

    @Builder
    @QueryProjection
    public NotificationResponse(Long id, String orderState, String deliveryState) {
        this.id = id;
        this.orderState=orderState;
        this.deliveryState = deliveryState;
    }

    public static NotificationResponse of (Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .orderState(notification.getOrder().getOrderState())
                .deliveryState(notification.getOrder().getDeliveryState())
                .build();
    }

}
