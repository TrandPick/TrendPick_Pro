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
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("trendpick/usr/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final Rq rq;
    private final NotificationService notificationService;
    private final OrderService orderService;
    @GetMapping("/list")
    @PreAuthorize("hasAuthority({'MEMBER'})")
    public String showList(Model model) {
        Member member=rq.getMember();

       List<Notification>notifications=notificationService.findByMember(member.getId());
       List<Notification> orderNotifications = notifications.stream()
                .filter(n ->!n.getOrderState().equals("미결제")) // 미결제시에는 알림 생성 x
                .collect(Collectors.toList());
        List<Notification> deliveryNotifications = notifications.stream()
                // 배송중, 배송완료에만 알림이 가도록, 배송전취소 상태일때 주문취소 알림만 가도록 설정
                .filter(n -> !n.getDeliveryState().equals("준비중")&&!n.getDeliveryState().equals("배송전취소"))
                .collect(Collectors.toList());
        List<OrderItem> orderItems=null;

        if(!notifications.isEmpty()) {
           orderItems= notifications.get(0).getOrder().getOrderItems();
        }

        model.addAttribute("orderNotifications", orderNotifications);
        model.addAttribute("deliveryNotifications", deliveryNotifications);
        model.addAttribute("orderItems",orderItems);
        return "trendpick/usr/notification/list";
    }


}