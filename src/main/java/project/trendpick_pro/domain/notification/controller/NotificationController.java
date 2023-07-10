package project.trendpick_pro.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.domain.notification.service.NotificationService;
import project.trendpick_pro.domain.orders.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("trendpick/usr/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final Rq rq;
    private final NotificationService notificationService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority({'MEMBER'})")
    public String showList(Model model) {
        Member member=rq.getMember();

       List<Notification>notifications=notificationService.findByMember(member.getId());
       List<Notification> orderNotifications = notifications.stream().toList();

        List<Notification> deliveryNotifications = notifications.stream()
                .filter(n -> !n.getDeliveryState().equals("준비중")&&!n.getDeliveryState().equals("배송전취소"))
                .collect(Collectors.toList());

        model.addAttribute("orderNotifications", orderNotifications);
        model.addAttribute("deliveryNotifications", deliveryNotifications);
        return "trendpick/usr/notification/list";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("delete/{notificationId}")
    public String removeItem(@PathVariable("notificationId") Long notificationId) {
        notificationService.removeNotification(notificationId);
        return rq.redirectWithMsg("/trendpick/usr/notification/list", "알림이 삭제되었습니다.");
    }
}