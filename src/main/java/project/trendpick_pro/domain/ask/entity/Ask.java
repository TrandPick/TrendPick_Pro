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

    @OneToMany(mappedBy = "ask", cascade = CascadeType.ALL)
    private List<Answer> answerList = new ArrayList<>();

    @Builder
    private Ask(String title, String content, AskStatus status) {
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public static Ask of(String title, String content) {
        return Ask.builder()
                .title(title)
                .content(content)
                .status(AskStatus.YET)
                .build();
    }

    public void connectMember(Member member) {
        this.author = member;
    }

    public void connectProduct(Product product) {
        this.product = product;
    }

    public void update(AskForm askForm) {
        this.title = askForm.getTitle();
        this.content = askForm.getContent();
    }
    public void updateStatus(){
        this.status = AskStatus.COMPLETED;
    }

    public void updateStatusYet() {
        this.status = AskStatus.YET;
    }
}