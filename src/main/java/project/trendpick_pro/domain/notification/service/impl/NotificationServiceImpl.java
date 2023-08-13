package project.trendpick_pro.domain.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.domain.notification.exception.NotificationNotFoundException;
import project.trendpick_pro.domain.notification.repository.NotificationRepository;
import project.trendpick_pro.domain.notification.service.NotificationService;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final OrderService orderService;

    @Transactional
    @Override
    public void create(Member member, Long orderId) {
        Order order=orderService.findById(orderId);
        if(!order.getOrderState().equals("미결제")) {
            Notification notification = Notification.of(member, order);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    @Override
    public RsData<Notification> updateStatus(Long orderId){
        Order order=orderService.findById(orderId);
        Notification notification=notificationRepository.findByOrderId(orderId);

        if(notification==null){
            return RsData.of("F-1","해당 주문내역은 없습니다.");
        }

        if (!notification.getNotificationType().equals(order.getOrderState())
                || !notification.getDeliveryState().equals(order.getDeliveryState())) {
            Notification newNotification = Notification.of(notification.getMember(), order);
            notificationRepository.save(newNotification);
        }
        return RsData.of("S-1","주문상태 업데이트 되었습니다.",notification);
    }

    @Transactional
    @Override
    public void delete(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new NotificationNotFoundException("해당 알림이 없습니다."));
        notificationRepository.delete(notification);
    }

    @Override
    public List<Notification> findByMember(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberId(memberId);
        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getCreatedDate).reversed())
                .collect(Collectors.toList());
    }
}