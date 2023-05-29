package project.trendpick_pro.domain.ask.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Ask extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ask_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String title;
    private String content;
    private String state;

    @OneToMany(mappedBy = "ask", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Answer> answerList = new ArrayList<>();

    public static Ask of(Member member, Product product, AskRequest askRequest) {
        return Ask.builder()
                .author(member)
                .product(product)
                .title(askRequest.getTitle())
                .content(askRequest.getTitle())
                .state("답변예정")
                .build()
                ;
    }

    public void update(AskRequest askRequest) {
        this.title = askRequest.getTitle();
        this.content = askRequest.getContent();
    }
}
