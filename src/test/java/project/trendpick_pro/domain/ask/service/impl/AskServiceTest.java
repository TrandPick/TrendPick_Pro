package project.trendpick_pro.domain.ask.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import project.trendpick_pro.IntegrationTestSupport;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.exception.AskNotFoundException;
import project.trendpick_pro.domain.ask.repository.AskRepository;
import project.trendpick_pro.domain.ask.service.AskService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class AskServiceTest extends IntegrationTestSupport {

    @Autowired
    private AskService askService;

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

    @DisplayName("상품에 대한 문의를 생성한다.")
    @Test
    void createAsk() throws Exception {
        //given
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(RoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        Product product = Product.builder()
                .title("nice shirt title")
                .description("nice shirt description")
                .build();
        Product savedProduct = productRepository.save(product);

        //when
        AskForm askForm = AskForm.builder()
                .title("new title")
                .content("new content")
                .productId(savedProduct.getId())
                .build();
        Long askId = askService.register(savedMember, askForm).getData();
        Ask findAsk = askRepository.findById(askId).orElseThrow(() -> new AskNotFoundException("해당 문의가 존재하지 않습니다."));

        //then
        assertThat(findAsk.getId()).isNotNull();
        assertThat(findAsk)
                .extracting("title", "content")
                .contains("new title", "new content");
    }

    @DisplayName("상품에 대한 문의 내용을 수정한다.")
    @Test
    void modifyAsk() throws Exception {
        //given
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(RoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        Product product = Product.builder()
                .title("nice shirt title")
                .description("nice shirt description")
                .build();
        Product savedProduct = productRepository.save(product);

        AskForm askForm = AskForm.builder()
                .title("new title")
                .content("new content")
                .productId(savedProduct.getId())
                .build();
        Long askId = askService.register(savedMember, askForm).getData();

        //when
        AskForm modifyAskForm = AskForm.builder()
                .title("modified title")
                .content("modified content")
                .productId(savedProduct.getId())
                .build();
        AskResponse modifyAsk = askService.modify(savedMember, askId, modifyAskForm).getData();

        //then
        assertThat(modifyAsk.getAskId()).isNotNull();
        assertThat(modifyAsk)
                .extracting("title", "content")
                .contains("modified title", "modified content");
    }

    @DisplayName("임의로 지정한 문의 내역을 확인한다.")
    @Test
    void findAsk() throws Exception {
        //given
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(RoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        Product product = Product.builder()
                .title("nice shirt title")
                .description("nice shirt description")
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
        List<Ask> asks = askRepository.saveAll(List.of(ask1, ask2, ask3));

        //when
        AskResponse response = askService.find(asks.get(1).getId());

        //then
        assertThat(response.getAskId()).isNotNull();
        assertThat(response)
                .extracting("title", "content")
                .contains("title2", "content2");
    }

    @DisplayName("임의로 지정한 상품에 대한 문의 내역을 확인한다.")
    @Test
    void findAsksByProduct() throws Exception {
        //given
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(RoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        Product product = Product.builder()
                .title("nice shirt title")
                .description("nice shirt description")
                .build();
        Product savedProduct = productRepository.save(product);

        Ask ask1 = Ask.of("title1", "content1");
        Ask ask2 = Ask.of("title2", "content2");
        Ask ask3 = Ask.of("title3", "content3");
        Ask ask4 = Ask.of("title4", "content4");
        Ask ask5 = Ask.of("title5", "content5");
        Ask ask6 = Ask.of("title6", "content6");
        Ask ask7 = Ask.of("title7", "content7");
        Ask ask8 = Ask.of("title8", "content8");

        ask1.connectMember(savedMember);
        ask2.connectMember(savedMember);
        ask3.connectMember(savedMember);
        ask4.connectMember(savedMember);
        ask5.connectMember(savedMember);
        ask6.connectMember(savedMember);
        ask7.connectMember(savedMember);
        ask8.connectMember(savedMember);

        ask1.connectProduct(savedProduct);
        ask2.connectProduct(savedProduct);
        ask3.connectProduct(savedProduct);
        ask4.connectProduct(savedProduct);
        ask5.connectProduct(savedProduct);
        ask6.connectProduct(savedProduct);
        ask7.connectProduct(savedProduct);
        ask8.connectProduct(savedProduct);
        askRepository.saveAll(List.of(ask1, ask2, ask3, ask4, ask5, ask6, ask7, ask8));

        //when
        Page<AskResponse> findAsks = askService.findAsksByProduct(savedProduct.getId(), 1);
        System.out.println("findAsks = " + findAsks.getContent());

        //then
        assertThat(findAsks.getTotalPages()).isEqualTo(2);
        assertThat(findAsks.getContent()).hasSize(3)
                .extracting("title", "content")
                .containsExactlyInAnyOrder(
                        tuple("title6", "content6"),
                        tuple("title7", "content7"),
                        tuple("title8", "content8")
                );
    }
}