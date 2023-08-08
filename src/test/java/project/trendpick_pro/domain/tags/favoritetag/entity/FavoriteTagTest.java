package project.trendpick_pro.domain.tags.favoritetag.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.trendpick_pro.domain.tags.tag.entity.TagType;
import static org.assertj.core.api.Assertions.assertThat;
class FavoriteTagTest {

    @DisplayName("increase(TagType.SHOW) -> +1점 상승 ")
    @Test
    void increaseBySHOW() throws Exception {
        //given
        FavoriteTag favoriteTag = new FavoriteTag("태그");

        //when
        favoriteTag.increaseScore(TagType.SHOW);

        //then
        assertThat(favoriteTag.getScore()).isEqualTo(1);
    }

    @DisplayName("increase(TagType.REGISTER) -> +30점 상승 ")
    @Test
    void increaseByREGISTER() throws Exception {
        //given
        FavoriteTag favoriteTag = new FavoriteTag("태그");

        //when
        favoriteTag.increaseScore(TagType.REGISTER);

        //then
        assertThat(favoriteTag.getScore()).isEqualTo(30);
    }

    @DisplayName("increase(TagType.ORDER) -> +10점 상승 ")
    @Test
    void increaseByORDER() throws Exception {
        //given
        FavoriteTag favoriteTag = new FavoriteTag("태그");

        //when
        favoriteTag.increaseScore(TagType.ORDER);

        //then
        assertThat(favoriteTag.getScore()).isEqualTo(10);
    }

    @DisplayName("increase(TagType.CART) -> +5점 상승 ")
    @Test
    void increaseCART() throws Exception {
        //given
        FavoriteTag favoriteTag = new FavoriteTag("태그");

        //when
        favoriteTag.increaseScore(TagType.CART);

        //then
        assertThat(favoriteTag.getScore()).isEqualTo(5);
    }
}