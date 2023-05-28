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

import java.time.LocalDateTime;
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

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "member_id")
    private String author; //Member

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "brand_id")
    private String brand; //Brand

    private String title;
    private String content;

    @OneToMany(mappedBy = "ask", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Answer> answerList = new ArrayList<>();

    public static Ask of(String member, String brand, AskRequest askRequest) {
        return Ask.builder()
                .author(member)
                .brand(brand)
                .title(askRequest.getTitle())
                .content(askRequest.getTitle())
                .build()
                ;
    }

    public void update(AskRequest askRequest) {
        this.title = askRequest.getTitle();
        this.content = askRequest.getContent();
    }
}
