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

import java.util.List;

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

        if(!order.getOrderState().equals("미결제") && !order.getOrderState().equals("")) {
            notification = Notification.of(member, order);
            notificationRepository.save(notification);
        }
        return RsData.of("S-1", "알림 메세지가 생성되었습니다.", NotificationResponse.of(notification));
    }
    public List<Notification> findByMember(Long memberId) {
        return notificationRepository.findByMemberId(memberId);
    }
}
