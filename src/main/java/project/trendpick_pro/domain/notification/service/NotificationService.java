package project.trendpick_pro.domain.notification.service;

import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.global.rsData.RsData;

import java.util.List;

public interface NotificationService {

    void make(Member member, Long orderId);
    RsData<Notification> updateStatus(Long orderId);
    void removeNotification(Long notificationId);
    List<Notification> findByMember(Long memberId);
}
