package project.trendpick_pro.domain.tags.favoritetag.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class FavoriteTagRepositoryTest {

    @Autowired
    FavoriteTagRepository favoriteTagRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원이 가지고 있는 FavoriteTag 조회")
    void findAllByMember() throws Exception{
        //given
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();

        for(int i=1; i<=10; i++){
            member.addTag(new FavoriteTag("태그" + i));
        }

        Member savedMember = memberRepository.save(member);

        //when
        Set<FavoriteTag> favoriteTagList = favoriteTagRepository.findAllByMember(savedMember);

        //then
        assertThat(favoriteTagList).isNotNull();
        assertThat(favoriteTagList.size()).isEqualTo(10);
    }


}