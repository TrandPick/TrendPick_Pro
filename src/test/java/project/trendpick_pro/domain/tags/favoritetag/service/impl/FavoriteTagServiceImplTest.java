package project.trendpick_pro.domain.tags.favoritetag.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.favoritetag.repository.FavoriteTagRepository;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.entity.TagType;
import project.trendpick_pro.domain.tags.tag.repository.TagRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FavoriteTagServiceImplTest {

    @InjectMocks
    protected FavoriteTagServiceImpl favoriteTagService;

    @Mock
    protected FavoriteTagRepository favoriteTagRepository;

    @Mock
    protected TagRepository tagRepository;

    static Member member;
    static Product product;
    static Set<Tag> tagList;
    static Set<FavoriteTag> favoriteTagList;

    @BeforeEach
    void beforeEach() {
        //given 공통
        member = createMember();
        product = mock(Product.class);

        //상품에 있는 태그목록
        tagList = new LinkedHashSet<>();
        for (int i = 1; i <= 10; i++) {
            tagList.add(new Tag("태그" + i));
        }

        //회원의 관심태그 목록
        for (int i = 1; i <= 5; i++) {
            FavoriteTag favoriteTag = new FavoriteTag("태그" + i);
            member.addTag(favoriteTag);
        }

    }

    @Test
    @DisplayName("상품 tag가 favoriteTag에 존재하지 않는게 있으면 favoriteTag가 추가된다.")
    void addTag() throws Exception {
        //given
        favoriteTagList = member.getTags();

        given(favoriteTagRepository.findAllByMember(any(Member.class))).willReturn(favoriteTagList);
        given(tagRepository.findAllByProduct(any(Product.class))).willReturn(tagList);

        //when
        favoriteTagService.updateTag(member, product, TagType.SHOW);

        //then
        assertThat(favoriteTagList.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("상품 tag가 favoriteTag에 존재하면 favoriteTag 점수가 업데이트 된다. (상품조회)")
    void updateTag1() throws Exception {
        //given
        favoriteTagList = member.getTags();

        given(favoriteTagRepository.findAllByMember(any(Member.class))).willReturn(favoriteTagList);
        given(tagRepository.findAllByProduct(any(Product.class))).willReturn(tagList);

        //when
        favoriteTagService.updateTag(member, product, TagType.SHOW); //+1
        favoriteTagService.updateTag(member, product, TagType.SHOW); //+1

        //then
        for (FavoriteTag favoriteTag : favoriteTagList) {
            assertThat(favoriteTag.getScore()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("상품 tag가 favoriteTag에 존재하면 favoriteTag 점수가 업데이트 된다. (장바구니담기)")
    void updateTag2() throws Exception {
        //given
        favoriteTagList = member.getTags();

        given(favoriteTagRepository.findAllByMember(any(Member.class))).willReturn(favoriteTagList);
        given(tagRepository.findAllByProduct(any(Product.class))).willReturn(tagList);

        //when
        favoriteTagService.updateTag(member, product, TagType.SHOW); //+1
        favoriteTagService.updateTag(member, product, TagType.CART); //+5

        //then
        for (FavoriteTag favoriteTag : favoriteTagList) {
            assertThat(favoriteTag.getScore()).isEqualTo(6);
        }
    }


    @Test
    @DisplayName("상품 tag가 favoriteTag에 존재하면 favoriteTag 점수가 업데이트 된다. (상품구매)")
    void updateTag3() throws Exception {

        favoriteTagList = member.getTags();

        given(favoriteTagRepository.findAllByMember(any(Member.class))).willReturn(favoriteTagList);
        given(tagRepository.findAllByProduct(any(Product.class))).willReturn(tagList);

        //when
        favoriteTagService.updateTag(member, product, TagType.CART); //+5
        favoriteTagService.updateTag(member, product, TagType.ORDER); //+10

        //then
        for (FavoriteTag favoriteTag : favoriteTagList) {
            assertThat(favoriteTag.getScore()).isEqualTo(15);
        }
    }

    @Test
    @DisplayName("상품이 가지고 있는 Tag가 아니면 favoriteTag 점수는 변함이 없다.")
    void updateTag4() throws Exception {
        member = createMember();
        //상품에 있는 태그목록
        tagList = new LinkedHashSet<>();
        for (int i = 1; i <= 10; i++) {
            tagList.add(new Tag("태그" + i));
        }

        //회원의 관심태그 목록
        for (int i = 11; i <= 20; i++) {
            FavoriteTag favoriteTag = new FavoriteTag("태그" + i);
            member.addTag(favoriteTag);
        }

        favoriteTagList = member.getTags();

        given(favoriteTagRepository.findAllByMember(any(Member.class))).willReturn(favoriteTagList);
        given(tagRepository.findAllByProduct(any(Product.class))).willReturn(tagList);

        //when
        favoriteTagService.updateTag(member, product, TagType.CART); //+5
        List<FavoriteTag> list = new ArrayList<>(favoriteTagList); //Set을 List로 변환


        //then
        //기존에 가지고 있던 태그들 (상품은 없는 태그)은 아무런 변화가 없다.
        for(int i=0; i<10; i++){
            assertThat(list.get(i).getScore()).isEqualTo(0);
        }

        //상품으로 인해 새로 생긴 태그는 점수가 +5점 상승한다.
        for(int i=10; i<20; i++){
            assertThat(list.get(i).getScore()).isEqualTo(5);
        }
    }


    private Member createMember() {
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();
        return member;
    }


}