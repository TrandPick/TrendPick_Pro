package project.trendpick_pro.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.domain.notification.service.NotificationService;

import java.util.List;

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
        model.addAttribute("notifications", notifications);
        return "trendpuck/usr/notification/list";
    }
}