package project.trendpick_pro.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.tag.entity.Tag;
import project.trendpick_pro.domain.tag.entity.type.TagType;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public FavoriteTag(Tag tag, String name) {
        this.tag = tag;
        this.name = name;
    }

    public void connectMember(Member member) {
        this.member = member;
    }

    public void increaseScore(TagType type){
        //유형에 따라 가중치
        switch (type) {
            case ORDER -> score += 10;
            case CART -> score += 5;
            default -> score += 1;
        }
    }

    public void decreaseScore(TagType type){
        //유형에 따라 가중치
        switch (type) {
            case ORDER -> score -= 10;
            case CART -> score -= 5;
            default -> score -= 1;
        }
    }
}
