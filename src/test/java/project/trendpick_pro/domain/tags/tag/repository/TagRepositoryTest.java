package project.trendpick_pro.domain.tags.tag.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class TagRepositoryTest {
    @Autowired
    TagRepository tagRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("findAllByProduct는 상품이 가지고 있는 Tag들을 모두 가져온다.")
    void findAllByProduct() throws Exception{
        //given
        Product product = Product.of("상품", "상품입니다.");

        Set<Tag> tagList = new LinkedHashSet<>();
        for(int i=1; i<=10; i++){
            tagList.add(new Tag("태그"+i));
        }

        product.updateTags(tagList);
        Product savedProduct = productRepository.save(product);

        //when
        Set<Tag> findTagList = tagRepository.findAllByProduct(savedProduct);

        //then
        assertThat(findTagList).isNotNull();
        assertThat(findTagList.size()).isEqualTo(tagList.size());
    }
}