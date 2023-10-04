package project.trendpick_pro.domain.orders.contoller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import project.trendpick_pro.ControllerTestSupport;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.domain.orders.entity.dto.request.ProductOrderRequest;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.global.util.rsData.RsData;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTestSupport {

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("로그인된 사용자는 장바구니로부터 주문을 요청한다.")
    @Test
    void cartToOrder() throws Exception {
        //given
        CartToOrderRequest request = new CartToOrderRequest(List.of(1L, 2L));
        given(orderService.cartToOrder(any(), any(CartToOrderRequest.class))).willReturn(RsData.successOf(1L));

        //when //then
        mockMvc.perform(
                        post("/trendpick/orders/order/cart")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("로그인된 사용자는 상품 페이지에서 직접 주문을 요청한다.")
    @Test
    void productToOrder() throws Exception {
        //given
        ProductOrderRequest request = new ProductOrderRequest(1L, 10, "L", "BLACK");
        given(orderService.productToOrder(any(), anyLong(), anyInt(), anyString(), anyString())).willReturn(RsData.successOf(1L));

        //when //then
        mockMvc.perform(
                        post("/trendpick/orders/order/product")
                                .param("productId", String.valueOf(request.getProductId()))
                                .param("size", request.getSize())
                                .param("quantity", String.valueOf(request.getQuantity()))
                                .param("color", request.getColor())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("로그인된 사용자는 본인의 주문 내역을 취소한다.")
    @Test
    void cancelOrder() throws Exception {
        //given
        given(orderService.cancel(anyLong())).willReturn(RsData.successOf(1L));

        //when //then
        mockMvc.perform(
                        post("/trendpick/orders/cancel/{orderId}", 1L)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("로그인된 사용자는 본인의 주문목록을 확인한다.")
    @Test
    void orderListByMember() throws Exception {
        //given
        List<OrderResponse> content = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderResponse> page = new PageImpl<>(content, pageable, content.size());


        given(orderService.findAll(any(Member.class), anyInt())).willReturn(page);

        //when //then
        mockMvc.perform(
                        get("/trendpick/orders/usr")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("offset", "0")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testUser", roles = {"BRAND_ADMIN"})
    @DisplayName("로그인된 브랜드 관리자는 본인 브랜드 제품들의 주문목록을 확인한다.")
    @Test
    void orderListBySeller() throws Exception {
        //given
        List<OrderResponse> content = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderResponse> page = new PageImpl<>(content, pageable, content.size());


        given(orderService.findAll(any(Member.class), anyInt())).willReturn(page);

        //when //then
        mockMvc.perform(
                        get("/trendpick/orders/admin")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("offset", "0")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("로그인된 사용자는 본인의 주문내역을 확인한다.")
    @Test
    void showOrder() throws Exception {
        //given
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();

        given(rq.getMember()).willReturn(member);
        given(orderService.findOrderItems(member, 1L)).willReturn(RsData.successOf(new OrderDetailResponse()));

        //when //then
        mockMvc.perform(
                        get("/trendpick/orders/{orderId}", 1L)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    @DisplayName("로그인된 사용자는 본인의 취소된 주문목록을 확인한다.")
    @Test
    void showCanceledOrderListByMember() throws Exception {
        //given
        given(orderService.findCanceledOrders(any(Member.class), anyInt())).willReturn(new PageImpl<>(new ArrayList<>()));

        //when //then
        mockMvc.perform(
                        get("/trendpick/orders/usr/refunds")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("offset", "0")
                )
                .andDo(print())
                .andExpect(status().isOk());

    }
}