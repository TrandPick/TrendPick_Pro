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
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderForm;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;
import project.trendpick_pro.global.basedata.tagname.entity.TagName;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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
            BrandService brandService,
            ProductRepository productRepository,
            OrderService orderservice
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

                //==상품데이터==//
                for(int n=1; n<=10; n++) {
                    CommonFile mainFile = CommonFile.builder()
                            .fileName("355d1034-90ac-420b-ae02-6656eeebd707.jpg")
                            .build();
                    List<CommonFile> subFiles = new ArrayList<>();
                    subFiles.add(CommonFile.builder()
                            .fileName("290ffec9-2da6-46dd-8779-86c27d48ef0c.jpg")
                            .build());

                    for (CommonFile subFile : subFiles) {
                        mainFile.connectFile(subFile);
                    }

                    Product product = Product
                            .builder()
                            .name("멋쟁이 티셔츠"+n)
                            .description("이 상품은 멋쟁이 티셔츠입니다."+n)
                            .stock(50+n)
                            .price(20000+n)
                            .mainCategory(mainCategoryService.findByName("상의"))
                            .subCategory(subCategoryService.findByName("반소매티셔츠"))
                            .brand(brandService.findByName("나이키"))
                            .file(mainFile)
                            .build();
                    Set<Tag> tags = new LinkedHashSet<>();  // 상품에 포함시킬 태크 선택하여 저장
                    for (int i = 1; i <= 5; i++) {
                        TagName tagName = tagNameService.findById(Long.valueOf(i+n));
                        tags.add(new Tag(tagName.getName()));
                    }
                    product.addTag(tags);
                    productRepository.save(product);
                }

                //==주문데이터==//
                Member findMember = memberService.findByEmail("trendpick@naver.com").get();
                MemberInfoDto memberInfo = new MemberInfoDto(findMember);
                List<OrderItemDto> orderItems = new ArrayList<>();

                orderItems.add(new OrderItemDto(productRepository.findById(1L).get(), "M", 5));
                orderItems.add(new OrderItemDto(productRepository.findById(2L).get(), "L", 3));
                orderItems.add(new OrderItemDto(productRepository.findById(3L).get(), "S", 2));

                OrderForm orderForm = new OrderForm(memberInfo, orderItems);
                orderForm.setPaymentMethod("신용카드");
                orderservice.order(findMember, orderForm);
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
