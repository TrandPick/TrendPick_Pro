package project.trendpick_pro.domain.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.global.security.SecurityConfig;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;
import java.util.Collections;
import java.util.List;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(SecurityConfig.class)
@ActiveProfiles("test")
@WebMvcTest(
        controllers = {
                CartController.class
        }
)
class CartControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected Rq rq;

    @MockBean
    protected CartService cartService;

    @Test
    @DisplayName("장바구니 조회 컨트롤러 요청")
    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    void showCart() throws Exception {
        Member member = createMember();
        Cart cart = new Cart(member);
        List<CartItem> cartItemList = Collections.singletonList(CartItem.builder().color("red").build()); //2개

        given(rq.getMember()).willReturn(member);
        given(cartService.getCartByUser(member.getId())).willReturn(cart);
        given(cartService.getAllCartItems(any(Cart.class))).willReturn(cartItemList);

        // 컨트롤러에 요청을 보내고 결과를 확인합니다.
        mockMvc.perform(
                        get("/trendpick/usr/cart/list")
                )
                .andDo(print())
                .andExpect(view().name("trendpick/usr/cart/list"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attribute("cartItems", cartItemList))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니에 상품을 추가하는 컨트롤러 요청")
    @WithMockUser(username = "testUser", roles = {"MEMBER"})
    void addItem() throws Exception {
        Member member = createMember();
        CartItemRequest request = new CartItemRequest(1L, "L", "red", 10);

        given(rq.getMember()).willReturn(member);
        given(cartService.addCartItem(any(Member.class), any(CartItemRequest.class))).willReturn(RsData.success());

        mockMvc.perform(post("/trendpick/usr/cart/add")
                        .param("productId", String.valueOf(request.getProductId()))
                        .param("size", request.getSize())
                        .param("color", request.getColor())
                        .param("quantity", String.valueOf(request.getQuantity())))
                .andDo(print())
                .andExpect(status().isOk());
//        verify(cartService).addCartItem(any(Member.class), request);
    }


    @Test
    @DisplayName("장바구니에 담긴 상품을 id값으로 삭제하는 컨트롤러 요청")
    @WithMockUser(username = "testUser", roles = {"MEMBER"})

    void removeItem() throws Exception {
        mockMvc.perform(get("/trendpick/usr/cart/delete/1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(cartService).deleteCartItem(1L);
    }

    @Test
    @DisplayName("장바구니에 담긴 상품의 수량을 변경하는 컨트롤러 요청")
    @WithMockUser(username = "testUser", roles = {"MEMBER"})

    void updateCount() throws Exception {
        Long cartItemId = 1L;
        int quantity = 10;

        given(cartService.updateCartItemCount(anyLong(), anyInt())).willReturn(RsData.success()); //return of("S-1", "성공");

        mockMvc.perform(post("/trendpick/usr/cart/update")
                .param("cartItemId", String.valueOf(cartItemId))
                .param("quantity", String.valueOf(quantity)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(cartService).updateCartItemCount(cartItemId, quantity);
    }

    private Member createMember() {
        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();
        return member;
    }
}