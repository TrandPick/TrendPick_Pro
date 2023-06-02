package project.trendpick_pro.global.basedata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tag.entity.Tag;
import project.trendpick_pro.domain.tag.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile({"dev", "test"})
public class BaseData {

    @Value("${tag}")
    List<String> tags;

    @Bean
    CommandLineRunner initData(
            TagRepository tagRepository,
            ProductRepository productRepository,
            MemberRepository memberRepository
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) {
//                for (String tag : tags) {
//                    tagRepository.save(new Tag(tag));
//                }

                //member1은 태그1, 태그2, 태그3 을 각각 10점 5점 1점 가지고 있다
                //상품1은 태그1을 가지고 있다
                //상품2는 태그1, 태그2를 가지고 있다
                //상품3은 태그1, 태그2 태그3 모두 가지고 있다.
                //상품4는 아무것도 가지고 있지 않다.

                Member member1 = memberRepository.save(Member.builder().username("member1").build());
                Product product1 = Product.builder().name("상품1").build();
                Product product2 = Product.builder().name("상품2").build();
                Product product3 = Product.builder().name("상품3").build();
                Product product4 = Product.builder().name("상품4").build();
                productRepository.save(product1);
                productRepository.save(product2);
                productRepository.save(product3);
                productRepository.save(product4);


                Tag tag1 = new Tag("태그1", member1);
                tag1.setScore(10);
                tagRepository.save(tag1);
                Tag tag2 = new Tag("태그2", member1);
                tag2.setScore(5);
                tagRepository.save(tag2);
                Tag tag3 = new Tag("태그3", member1);
                tag3.setScore(1);
                tagRepository.save(tag3);

                //상품1은 태그1을 가지고 있다
                Tag tag1ByProduct1 = new Tag("태그1", product1);
                tagRepository.save(tag1ByProduct1);

                //상품2는 태그1, 태그2를 가지고 있다
                Tag tag2ByProduct1 = new Tag("태그1", product2);
                Tag tag2ByProduct2 = new Tag("태그2", product2);
                tagRepository.save(tag2ByProduct1);
                tagRepository.save(tag2ByProduct2);

                //상품3은 태그1, 태그2 태그3 모두 가지고 있다.
                Tag tag3ByProduct1 = new Tag("태그1", product3);
                Tag tag3ByProduct2 = new Tag("태그2", product3);
                Tag tag3ByProduct3 = new Tag("태그3", product3);
                tagRepository.save(tag3ByProduct1);
                tagRepository.save(tag3ByProduct2);
                tagRepository.save(tag3ByProduct3);


//                List<Tag> tags1 = new ArrayList<>();
//                for(int i=0; i<10; i++){
//                    Tag tag = new Tag("태그" + i, member1);
//                    tag.setScore(10);
//                    tagRepository.save(tag);
//                    tags1.add(tag);
//                }
//                List<Tag> tags2 = new ArrayList<>();
//                for(int i=10; i<20; i++){
//                    Tag tag = new Tag("태그" + i, member2);
//                    tag.setScore(10);
//                    tagRepository.save(tag);
//                    tags2.add(tag);
//                }
//
//                List<Tag> tags3 = new ArrayList<>();
//                for(int i=1; i<5; i++){
//                    Tag tag = new Tag("태그" + i, product1);
//                    tagRepository.save(tag);
//                    tags3.add(tag);
//                }
//                List<Tag> tags4 = new ArrayList<>();
//                for(int i=5; i<10; i++){
//                    Tag tag = new Tag("태그" + i, product1);
//                    tagRepository.save(tag);
//                    tags3.add(tag);
//                }


            }
        };
    }
}
