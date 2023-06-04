package project.trendpick_pro.global.basedata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.service.MainCategoryService;
import project.trendpick_pro.domain.category.service.SubCategoryService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;
import project.trendpick_pro.domain.tags.tag.service.TagService;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
@Profile({"dev", "test"})
public class BaseData {

    @Value("${tag}")
    List<String> tags;

    @Value("${main-category}")
    List<String> mainCategories;

    @Value("${sub-category-1}")
    List<String> subCategories1;

    @Value("${brand}")
    List<String> brands;

    @Bean
    CommandLineRunner initData(
            TagService tagService,
            ProductRepository productRepository,
            MemberRepository memberRepository,
            MainCategoryService mainCategoryService,
            SubCategoryService subCategoryService,
            BrandService brandService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) {
                //원활한 테스트를 위해 잠깐 주석처리
//                for (String tag : tags) {
//                    tagService.save(tag);
//                }

                for (String mainCategory : mainCategories) {
                    mainCategoryService.save(mainCategory);
                }

                for (String subCategory : subCategories1) {
                    MainCategory mainCategory = mainCategoryService.findByName("상의");
                    subCategoryService.save(subCategory, mainCategory);
                }

                for (String brand : brands) {
                    brandService.save(brand);
                }

//                member1은 시티보이룩(10점), 빈티지룩(5점), 로멘틱룩(1점)을 선호 태그로 가지고 있다.
//                상품1은 시티보이룩을 가지고 있다
//                상품2는 시티보이룩, 빈티지룩을 가지고 있다
//                상품3은 시티보이룩, 빈티지룩, 로멘틱룩을 모두 가지고 있다.
//
                Member member1 = memberRepository.save(Member.builder().username("member1").email("jjj@naver.com").phoneNumber("01099999999").role(RoleType.MEMBER).password("111111").build());
                FavoriteTag favorTag1 = new FavoriteTag("시티보이룩");
                favorTag1.increaseScore(TagType.ORDER);//주문해서 10점 누적.
                member1.addTag(favorTag1);

                FavoriteTag favorTag2 = new FavoriteTag("빈티지룩");
                favorTag2.increaseScore(TagType.CART);//장바구니 담아서 5점 누적.
                member1.addTag(favorTag2);

                FavoriteTag favorTag3 = new FavoriteTag("로멘틱룩");
                favorTag3.increaseScore(TagType.ORDER);//상품 클릭해서 1점 누적.
                member1.addTag(favorTag3);

//                상품1은 시티보이룩을 가지고 있다
                Set<Tag> tags1 = new LinkedHashSet<>();
                Product product1 = Product.builder().name("상품1").tags(tags1).description("설명1").price(500).stock(100).build();
                product1.addTag(new Tag("시티보이룩"));

//                상품2는 시티보이룩, 빈티지룩을 가지고 있다
                Set<Tag> tags2 = new LinkedHashSet<>();
                Product product2 = Product.builder().name("상품2").tags(tags2).description("설명2").price(500).stock(100).build();
                product2.addTag(new Tag("시티보이룩"));
                product2.addTag(new Tag("빈지티룩"));

                Set<Tag> tags3 = new LinkedHashSet<>();
                Product product3 = Product.builder().name("상품3").tags(tags3).description("설명3").price(500).stock(100).build();
                product3.addTag(new Tag("시티보이룩"));
                product3.addTag(new Tag("빈지티룩"));
                product3.addTag(new Tag("로멘틱룩"));
                productRepository.save(product1);
                productRepository.save(product2);
                productRepository.save(product3);

//                상품3은 시티보이룩, 빈티지룩, 로멘틱룩을 모두 가지고 있다.
//                Set<Tag> tags3 = new LinkedHashSet<>();
//                tags3.add(new Tag("시티보이룩"));
//                tags3.add(new Tag("빈티지룩"));
//                tags3.add(new Tag("로멘틱룩"));
//                Product product3 = Product.builder().name("상품3").tags(tags3).description("설명3").price(500).stock(100).build();
//

//                T
            }
        };
    }
}
