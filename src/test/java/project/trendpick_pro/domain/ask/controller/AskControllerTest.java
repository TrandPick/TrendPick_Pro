package project.trendpick_pro.domain.ask.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import project.trendpick_pro.ControllerTestSupport;
import project.trendpick_pro.domain.ask.entity.dto.form.AskForm;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.service.impl.AskServiceImpl;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.global.util.rsData.RsData;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AskControllerTest extends ControllerTestSupport {

    @Mock
    protected MemberRepository memberRepository;

    @MockBean
    protected AskServiceImpl askService;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAllInBatch();
    }

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("로그인된 사용자는 신규 문의를 등록한다.")
    @Test
    void registerAsk() throws Exception {
        //given
        Member member = createMember();
        AskForm request = AskForm.builder()
                .productId(1L)
                .title("ask title")
                .content("ask content")
                .build();
        given(askService.register(member, request)).willReturn(RsData.successOf(1L));

        //when //then
        mockMvc.perform(
                post("/trendpick/asks/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productId", "1")
                        .param("title", "ask title")
                        .param("content", "ask content")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("로그인된 사용자는 문의 내용을 수정한다.")
    @Test
    void modifyAsk() throws Exception {
        //given
        Member member = createMember();
        AskForm request = AskForm.builder()
                .productId(1L)
                .title("ask title")
                .content("ask content")
                .build();
        given(askService.modify(member, 1L, request)).willReturn(RsData.of("S-1", "상품 문의글 수정이 정상적으로 처리되었습니다.", new AskResponse()));

        //when //then
        mockMvc.perform(
                        post("/trendpick/asks/edit/" + 1)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("askId", "1")
                                .param("productId", "1")
                                .param("title", "ask title")
                                .param("content", "ask content")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("누구나 상품에 대한 문의 내용을 조회할 수 있다.")
    @Test
    void showAsk() throws Exception {
        //given
        given(askService.find(1L)).willReturn(new AskResponse());

        //when //then
        mockMvc.perform(
                        get("/trendpick/asks/" + 1)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    private Member createMember() {
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(RoleType.MEMBER)
                .brand("Polo")
                .build();
        return memberRepository.save(member);
    }
}