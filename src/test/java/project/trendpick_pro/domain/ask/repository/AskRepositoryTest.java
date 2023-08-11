package project.trendpick_pro.domain.ask.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.IntegrationTestSupport;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class AskRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private AskRepository askRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        askRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("연결 정보로 상품과 유저가 존재할 때, 특정 상품에 대한 문의목록을 조회한다.")
    @Test
    void findAllWithProductIdAndMember() throws Exception {
        //given
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        Product product = Product.builder()
                .productCode(UUID.randomUUID().toString())
                .title("이쁜셔츠의 이름")
                .description("이쁜셔츠의 내용")
                .build();
        Product savedProduct = productRepository.save(product);

        Ask ask1 = Ask.of("title1", "content1");
        Ask ask2 = Ask.of("title2", "content2");
        Ask ask3 = Ask.of("title3", "content3");

        ask1.connectMember(savedMember);
        ask2.connectMember(savedMember);
        ask3.connectMember(savedMember);

        ask1.connectProduct(savedProduct);
        ask2.connectProduct(savedProduct);
        ask3.connectProduct(savedProduct);
        askRepository.saveAll(List.of(ask1, ask2, ask3));

        //when
        Pageable pageable = PageRequest.of(0, 2);
        Page<AskResponse> asks = askRepository.findAllByProductId(savedProduct.getId(), pageable);

        //then
        assertThat(asks.getTotalPages()).isEqualTo(2);
        assertThat(asks.getContent()).hasSize(2);
        assertThat(asks.getContent())
                .extracting("title", "content")
                .containsExactlyInAnyOrder(
                        tuple("title1", "content1"),
                        tuple("title2", "content2")
                );
    }

    @DisplayName("연결 정보로 상품과 유저 중 하나라도 존재하지 않을 때, 특정 상품에 대한 문의목록을 조회하지 못한다.")
    @Test
    void findAllWithOutProductIdAndMember() throws Exception {
        //given
        Product product = Product.builder()
                .productCode(UUID.randomUUID().toString())
                .title("이쁜셔츠의 이름")
                .description("이쁜셔츠의 내용")
                .build();
        Product savedProduct = productRepository.save(product);

        Ask ask1 = Ask.of("title1", "content1");
        Ask ask2 = Ask.of("title2", "content2");
        Ask ask3 = Ask.of("title3", "content3");
        ask1.connectProduct(savedProduct);
        ask2.connectProduct(savedProduct);
        ask3.connectProduct(savedProduct);
        askRepository.saveAll(List.of(ask1, ask2, ask3));

        //when
        Pageable pageable = PageRequest.of(0, 2);
        Page<AskResponse> asks = askRepository.findAllByProductId(savedProduct.getId(), pageable);

        //then
        assertThat(asks.getTotalPages()).isZero();
        assertThat(asks.getContent().size()).isZero();
    }
}