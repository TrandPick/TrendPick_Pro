package project.trendpick_pro.domain.notification.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.domain.notification.entity.dto.request.NotificationRequest;
import project.trendpick_pro.domain.notification.service.NotificationService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final Rq rq;

    // 알림 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/notification")
    public String createNotification(@RequestBody NotificationRequest request) {
        Member member=rq.getMember();
        notificationService.create(member, request);
        return "redirect:/trendpick/notification";
    }

    // 알림 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.delete(notificationId);
    }
}