package project.trendpick_pro.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.domain.notification.service.NotificationService;
import project.trendpick_pro.global.util.rq.Rq;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("trendpick/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final Rq rq;
    private final NotificationService notificationService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority({'MEMBER'})")
    public String findAll(Model model) {
        List<Notification>notifications=notificationService.findByMember(rq.getMember().getId());

        model.addAttribute("orderNotifications", notifications.stream().toList());
        model.addAttribute("deliveryNotifications",
            notifications.stream()
            .filter(n -> !n.getDeliveryState().equals("준비중")&&!n.getDeliveryState().equals("배송전취소"))
            .collect(Collectors.toList())
        );
        return "trendpick/usr/notification/list";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/{notificationId}")
    public String remove(@PathVariable("notificationId") Long notificationId) {
        notificationService.delete(notificationId);
        return rq.redirectWithMsg("/trendpick/usr/notification/list", "알림이 삭제되었습니다.");
    }
}