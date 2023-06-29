package project.trendpick_pro.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.notification.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Notification findByOrderId(Long orderId);
    List<Notification> findByMemberId(Long memberId);

    Notification findByMemberIdAndOrderId(Long memberId, long orderId);
}
