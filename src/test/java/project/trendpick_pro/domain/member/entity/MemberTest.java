package project.trendpick_pro.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @DisplayName("멤버 객체 생성시에 저장되는 태그들의 타임은 REGISTER로 저장되어 기본 점수를 부여받는다.")
    @Test
    void updateTagsScore() throws Exception {
        //given
        Set<FavoriteTag> tags = Set.of(
                new FavoriteTag("tag1"),
                new FavoriteTag("tag2"),
                new FavoriteTag("tag3")
        );

        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();

        //when
        member.changeTags(tags);

        //then
        assertThat(member.getTags()).hasSize(3)
                .extracting("score")
                .containsExactlyInAnyOrder(30, 30, 30);
    }
}