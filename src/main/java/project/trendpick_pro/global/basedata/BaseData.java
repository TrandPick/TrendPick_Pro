package project.trendpick_pro.global.basedata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.service.MainCategoryService;
import project.trendpick_pro.domain.category.service.SubCategoryService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;

import java.io.File;
import java.io.FileInputStream;
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

    @Value("${top}")
    List<String> tops;

    @Value("${outer}")
    List<String> outers;

    @Value("${bottom}")
    List<String> bottoms;

    @Value("${shoes}")
    List<String> shoes;

    @Value("${bag}")
    List<String> bags;

    @Value("${accessory}")
    List<String> accessories;

    @Value("${brand}")
    List<String> brands;

    @Bean
    CommandLineRunner initData(
            TagNameService tagNameService,
            MemberService memberService,
            MainCategoryService mainCategoryService,
            SubCategoryService subCategoryService,
            BrandService brandService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) {
                //원활한 테스트를 위해 잠깐 주석처리
                for (String tag : tags) {
                    tagNameService.save(tag);
                }

                for (String mainCategory : mainCategories) {
                    mainCategoryService.save(mainCategory);
                }

                CreateSubCategories(mainCategoryService, subCategoryService);

                for (String brand : brands) {
                    brandService.save(brand);
                }

                JoinForm admin = JoinForm.builder()
                        .email("admin@naver.com")
                        .password("12345")
                        .username("admin")
                        .phoneNumber("010-1234-1234")
                        .state("ADMIN")
                        .build();

                JoinForm brand_admin = JoinForm.builder()
                        .email("brand@naver.com")
                        .password("12345")
                        .username("brand")
                        .phoneNumber("010-1234-1234")
                        .state("BRAND_ADMIN")
                        .build();

                JoinForm member = JoinForm.builder()
                        .email("trendpick@naver.com")
                        .password("12345")
                        .username("sooho")
                        .phoneNumber("010-1234-1234")
                        .state("MEMBER")
                        .tags(List.of("오버핏청바지", "로맨틱룩"))
                        .build();

                memberService.register(admin);
                memberService.register(brand_admin);
                memberService.register(member);

//                member1은 시티보이룩(10점), 빈티지룩(5점), 로멘틱룩(1점)을 선호 태그로 가지고 있다.
//                상품1은 시티보이룩을 가지고 있다
//                상품2는 시티보이룩, 빈티지룩을 가지고 있다
//                상품3은 시티보이룩, 빈티지룩, 로멘틱룩을 모두 가지고 있다.
//
//                Member member1 = memberRepository.save(Member.builder().username("member1").email("jjj@naver.com").phoneNumber("01099999999").role(RoleType.MEMBER).password("111111").build());
//                FavoriteTag favorTag1 = new FavoriteTag("시티보이룩");
//                favorTag1.increaseScore(TagType.SHOW);//상품클릭해서 1점 누적.
//                member1.addTag(favorTag1);
//
//                FavoriteTag favorTag2 = new FavoriteTag("빈티지룩");
//                favorTag2.increaseScore(TagType.CART);//장바구니 담아서 5점 누적.
//                member1.addTag(favorTag2);
//
//                FavoriteTag favorTag3 = new FavoriteTag("로멘틱룩");
//                favorTag3.increaseScore(TagType.ORDER);//상품 주문해서 10점 누적.
//                member1.addTag(favorTag3);
//
////                상품1은 시티보이룩을 가지고 있다
//                Set<Tag> tags1 = new LinkedHashSet<>();
//                Product product1 = Product.builder().name("상품1").tags(tags1).description("설명1").price(500).stock(100).build();
//                product1.addTag(new Tag("시티보이룩"));
//
////                상품2는 시티보이룩, 빈티지룩을 가지고 있다
//                Set<Tag> tags2 = new LinkedHashSet<>();
//                Product product2 = Product.builder().name("상품2").tags(tags2).description("설명2").price(500).stock(100).build();
//                product2.addTag(new Tag("시티보이룩"));
//                product2.addTag(new Tag("빈티지룩"));
//
//                Set<Tag> tags3 = new LinkedHashSet<>();
//                Product product3 = Product.builder().name("상품3").tags(tags3).description("설명3").price(500).stock(100).build();
//                product3.addTag(new Tag("시티보이룩"));
//                product3.addTag(new Tag("빈티지룩"));
//                product3.addTag(new Tag("로멘틱룩"));
//                productService.save(product1);
//                productService.save(product2);
//                productService.save(product3);
//
//                Set<Tag> tags4 = new LinkedHashSet<>();
//                Product product4 = Product.builder().name("상품4").tags(tags4).description("설명4").price(500).stock(100).build();
//                product4.addTag(new Tag("원숭이룩"));
//                productService.save(product1);
//                productService.save(product2);
//                productService.save(product3);
//                productService.save(product4);
                //member1의 추천상품을 가져오면 로멘틱룩, 빈티지룩, 시티보이룩 순서대로 가져와야 한다.
                //상품 4는 member의 선호태그를 가지고 있지 않으므로 추천되지 않아야 한다.

//                상품3은 시티보이룩, 빈티지룩, 로멘틱룩을 모두 가지고 있다.
//                Set<Tag> tags3 = new LinkedHashSet<>();
//                tags3.add(new Tag("시티보이룩"));
//                tags3.add(new Tag("빈티지룩"));
//                tags3.add(new Tag("로멘틱룩"));
//                Product product3 = Product.builder().name("상품3").tags(tags3).description("설명3").price(500).stock(100).build();
            }
        };
    }

    private void CreateSubCategories(MainCategoryService mainCategoryService, SubCategoryService subCategoryService) {
        for (String top : tops) {
            MainCategory mainCategory = mainCategoryService.findByName("상의");
            subCategoryService.save(top, mainCategory);
        }

        for (String outer : outers) {
            MainCategory mainCategory = mainCategoryService.findByName("아우터");
            subCategoryService.save(outer, mainCategory);
        }

        for (String bottom : bottoms) {
            MainCategory mainCategory = mainCategoryService.findByName("하의");
            subCategoryService.save(bottom, mainCategory);
        }

        for (String shoe : shoes) {
            MainCategory mainCategory = mainCategoryService.findByName("신발");
            subCategoryService.save(shoe, mainCategory);
        }

        for (String bag : bags) {
            MainCategory mainCategory = mainCategoryService.findByName("가방");
            subCategoryService.save(bag, mainCategory);
        }

        for (String accessory : accessories) {
            MainCategory mainCategory = mainCategoryService.findByName("악세서리");
            subCategoryService.save(accessory, mainCategory);
        }
    }
}
