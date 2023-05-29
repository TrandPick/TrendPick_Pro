package project.trendpick_pro.domain.notification.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.answer.entity.dto.request.AnswerRequest;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.domain.notification.entity.dto.request.NotificationRequest;
import project.trendpick_pro.domain.notification.repository.NotificationRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    // 알림 생성
    @Transactional
    public void create(Member member, NotificationRequest notificationRequest) {
        Notification notification=Notification.of(member, notificationRequest);
        notificationRepository.save(notification);
    }

    // 알림 삭제
    public void delete(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}