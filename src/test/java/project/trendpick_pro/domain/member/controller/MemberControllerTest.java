package project.trendpick_pro.domain.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import project.trendpick_pro.ControllerTestSupport;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.response.MemberInfoResponse;
import project.trendpick_pro.domain.member.entity.form.AddressForm;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.global.util.rsData.RsData;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestSupport {

    @DisplayName("회원가입을 진행한다.")
    @Test
    void join() throws Exception {
        //given
        JoinForm form = JoinForm.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .state("MEMBER")
                .build();
        given(memberService.join(any(JoinForm.class)))
                .willReturn(RsData.successOf(anyLong()));

        //when //then
        mockMvc.perform(post("/trendpick/member/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", form.email())
                .param("password", form.password())
                .param("username", form.username())
                .param("phoneNumber", form.phoneNumber())
                .param("state", form.state())
                .with(anonymous())
        )
        .andDo(print())
        .andExpect(status().isOk());
    }

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("MEMBER 권한을 가진 사용자는 본인의 주소를 수정할 수 있다.")
    @Test
    void modifyAddress() throws Exception {
        //given
        AddressForm addressForm = AddressForm.builder()
                .address("서울시 강남구")
                .build();

        MemberInfoResponse memberInfoResponse = new MemberInfoResponse();
        given(memberService.modifyAddress(any(Member.class), anyString()))
                .willReturn(RsData.successOf(memberInfoResponse));

        //when //then
        mockMvc.perform(post("/trendpick/member/edit/address")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("address", addressForm.address())
        )
        .andDo(print())
        .andExpect(status().isOk());
    }
}