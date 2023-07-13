package project.trendpick_pro.domain.ask.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private AskStatus status;

    @OneToMany(mappedBy = "ask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList = new ArrayList<>();

    @Builder
    private Ask(Member author, Product product, String title, String content, AskStatus status) {
        this.author = author;
        this.product = product;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public static Ask of(Member member, Product product, AskForm askForm) {
        return Ask.builder()
                .author(member)
                .product(product)
                .title(askForm.getTitle())
                .content(askForm.getContent())
                .status(AskStatus.YET)
                .build();
    }

    public void update(AskForm askForm) {
        this.title = askForm.getTitle();
        this.content = askForm.getContent();
    }
    public void changeStatus(){
        this.status = AskStatus.COMPLETED;
    }

    public void changeStatusYet() {
        this.status = AskStatus.YET;
    }
}