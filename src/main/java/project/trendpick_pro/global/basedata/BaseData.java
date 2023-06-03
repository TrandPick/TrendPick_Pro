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
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tag.service.TagService;

import java.util.ArrayList;
import java.util.List;

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
                for (String tag : tags) {
                    tagService.save(tag);
                }

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

                //member1은 태그1, 태그2, 태그3 을 각각 10점 5점 1점 가지고 있다
                //상품1은 태그1을 가지고 있다
                //상품2는 태그1, 태그2를 가지고 있다
                //상품3은 태그1, 태그2 태그3 모두 가지고 있다.
//
//                Member member1 = memberRepository.save(Member.builder().username("member1").build());
//                Product product1 = Product.builder().name("상품1").build();
//                Product product2 = Product.builder().name("상품2").build();
//                Product product3 = Product.builder().name("상품3").build();
//                Product product4 = Product.builder().name("상품4").build();
//                productRepository.save(product1);
//                productRepository.save(product2);
//                productRepository.save(product3);
//                productRepository.save(product4);
//
//
//                Tag tag1 = new Tag("태그1", member1);
//                tag1.changeScore(10);
//                tagRepository.save(tag1);
//                Tag tag2 = new Tag("태그2", member1);
//                tag2.changeScore(5);
//                tagRepository.save(tag2);
//                Tag tag3 = new Tag("태그3", member1);
//                tag3.changeScore(1);
//                tagRepository.save(tag3);
//
//                //상품1은 태그1을 가지고 있다
//                Tag tag1ByProduct1 = new Tag("태그1", product1);
//                tagRepository.save(tag1ByProduct1);
//
//                //상품2는 태그1, 태그2를 가지고 있다
//                Tag tag2ByProduct1 = new Tag("태그1", product2);
//                Tag tag2ByProduct2 = new Tag("태그2", product2);
//                tagRepository.save(tag2ByProduct1);
//                tagRepository.save(tag2ByProduct2);
//
//                //상품3은 태그1, 태그2 태그3 모두 가지고 있다.
//                Tag tag3ByProduct1 = new Tag("태그1", product3);
//                Tag tag3ByProduct2 = new Tag("태그2", product3);
//                Tag tag3ByProduct3 = new Tag("태그3", product3);
//                tagRepository.save(tag3ByProduct1);
//                tagRepository.save(tag3ByProduct2);
//                tagRepository.save(tag3ByProduct3);
            }
        };
    }
}
