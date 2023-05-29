package project.trendpick_pro.domain.notification.entity;

import com.querydsl.core.types.Order;
import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.notification.entity.dto.request.NotificationRequest;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Notification {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "answer_id")
        private Answer answer;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id")
        private Member member;

        //@ManyToOne(fetch = FetchType.LAZY)
       // @JoinColumn(name = "order_id")
        private Order order;

        private String title; // 제목

        private String content; // 내용

        public static Notification of(Member member, NotificationRequest notificationRequest) {
                return Notification.builder()
                        .member(member)
                        .title(notificationRequest.getTitle())
                        .content(notificationRequest.getContent())
                        .build()
                        ;
        }
}
