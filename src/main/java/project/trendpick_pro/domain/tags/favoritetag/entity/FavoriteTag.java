package project.trendpick_pro.domain.tags.favoritetag.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.tags.tag.entity.TagType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int score = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public FavoriteTag(String name) {
        this.name = name;
    }

    public void connectMember(Member member){
        this.member = member;
    }

    public void increaseScore(TagType type){
        switch (type) {
            case ORDER -> score += 10;
            case CART -> score += 5;
            case REGISTER -> score += 30;
            default -> score += 1;
        }
    }

    public void decreaseScore(TagType type){
        switch (type) {
            case ORDER -> score -= 10;
            case CART -> score -= 5;
            case REGISTER -> score -= 30;
            default -> score -= 1;
        }
    }
}
