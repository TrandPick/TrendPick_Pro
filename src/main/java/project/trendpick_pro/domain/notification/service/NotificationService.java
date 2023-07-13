package project.trendpick_pro.domain.notification.service;

import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

public interface NotificationService {

    void create(Member member, Long orderId);
    RsData<Notification> updateStatus(Long orderId);
    void delete(Long notificationId);
    List<Notification> findByMember(Long memberId);
}
