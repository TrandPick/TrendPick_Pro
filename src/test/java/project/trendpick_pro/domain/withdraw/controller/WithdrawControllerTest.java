package project.trendpick_pro.domain.withdraw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import project.trendpick_pro.domain.cart.controller.CartController;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.withdraw.entity.dto.WithDrawApplyForm;
import project.trendpick_pro.domain.withdraw.service.WithdrawService;
import project.trendpick_pro.global.security.SecurityConfig;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Import(SecurityConfig.class)
@ActiveProfiles("test")
@WebMvcTest(
        controllers = {
                WithdrawController.class
        }
)
class WithdrawControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected Rq rq;
    @MockBean
    protected WithdrawService withdrawService;

    @Test
    @DisplayName("출금 신청 폼 컨트롤러")
    @WithMockUser(username = "testUser", roles = {"BRAND_ADMIN"})
    void showApplyForm() throws Exception {
        //given
        Member member = createMember();
        int restCash = 10000;
        given(rq.getBrandMember()).willReturn(member);
        given(withdrawService.showRestCash(any(Member.class))).willReturn(restCash);

        //when
        mockMvc.perform(
                        get("/trendpick/admin/withDraw")
                )
                .andDo(print())
                .andExpect(view().name("trendpick/admin/withDraw"))
                .andExpect(model().attributeExists("actorRestCash"))
                .andExpect(model().attribute("actorRestCash", restCash))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("출금 신청 컨트롤러")
    @WithMockUser(username = "testUser", roles = {"BRAND_ADMIN"})
    void apply() throws Exception {
        //given
        Member member = mock(Member.class);
        WithDrawApplyForm withDrawApplyForm = new WithDrawApplyForm("국민은행", "123-456-789", 10000);
        given(rq.getBrandMember()).willReturn(member);
        given(withdrawService.apply(any(WithDrawApplyForm.class), any(Member.class))).willReturn(RsData.success());

        //when
        mockMvc.perform(
                post("/trendpick/admin/withDraw")
                        .param("bankName", withDrawApplyForm.getBankName())
                        .param("bankAccountNo", withDrawApplyForm.getBankAccountNo())
                        .param("price", String.valueOf(withDrawApplyForm.getPrice()))
                )
                .andDo(print())
                .andExpect(status().isOk());

        //then
        verify(withdrawService).apply(withDrawApplyForm, member);
    }



    private Member createMember() {
        Member member = Member.builder()
                .email("brand@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.BRAND_ADMIN)
                .brand("Polo")
                .build();
        return member;
    }
}