package project.trendpick_pro.domain.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.Notification;
import project.trendpick_pro.domain.notification.entity.dto.response.NotificationResponse;
import project.trendpick_pro.domain.notification.repository.NotificationRepository;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.rsData.RsData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final NotificationRepository notificationRepository;
    @Transactional
    public RsData<NotificationResponse> make(Member member, Long orderId) {
        Order order=orderService.findById(orderId);
        Notification notification=notificationRepository.findByMemberIdAndOrderId(member.getId(),order.getId());

        if(order==null){
            return RsData.of("F-1","해당 주문은 존재하지 않습니다.");
        }
        if(!order.getOrderState().equals("미결제")) {
            notification = Notification.of(member, order);
            notificationRepository.save(notification);
        }
        return RsData.of("S-1", "알림 메세지가 생성되었습니다.", NotificationResponse.of(notification));
    }

    public List<Notification> findByMember(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberId(memberId);
        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getCreateDate).reversed()) // 생성일자 내림차순 정렬
                .collect(Collectors.toList());
    }

    @Transactional
    public RsData<Notification> updateStatus(Long orderId){
        Order order=orderService.findById(orderId);
        Notification notification=notificationRepository.findByOrderId(orderId);

        if(notification==null){
            return RsData.of("F-1","해당 주문내역은 없습니다.");
        }
        if (!notification.getOrderState().equals(order.getOrderState())
                || !notification.getDeliveryState().equals(order.getDeliveryState())) {
            Notification newNotification = Notification.of(notification.getMember(), order);
            notificationRepository.save(newNotification);
        }


      //  notification.updateOrderState(order.getOrderState(), order.getDeliveryState());
        return RsData.of("S-1","주문상태 업데이트 되었습니다.",notification);
    }

    @Transactional
    public void removeNotification(Long notifiactionId) {
        notificationRepository.deleteById(notifiactionId);
    }

}
