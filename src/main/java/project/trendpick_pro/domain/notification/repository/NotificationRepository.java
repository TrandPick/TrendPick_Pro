package project.trendpick_pro.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
