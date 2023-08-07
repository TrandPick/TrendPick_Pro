package project.trendpick_pro.domain.cart.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.cart.repository.CartItemRepository;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.global.util.rsData.RsData;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @InjectMocks
    protected CartServiceImpl cartService;

    @Mock
    protected CartRepository cartRepository;
    @Mock
    protected CartItemRepository cartItemRepository;

    @Mock
    protected ProductService productService;
    @Mock
    protected FavoriteTagService favoriteTagService;




    @DisplayName("상품을 장바구니에 담는다. 요청 수량만큼 총수량이 증가한다.(장바구니에 해당 상품이 존재하지 않는다.)")
    @Test
    void addCartItem() throws Exception {
        //given
        Long id = 1L; //공용

        Member member = mock(Member.class);
        Product product = mock(Product.class);
        Cart cart = new Cart(member);

        given(member.getId()).willReturn(id);
        given(product.getId()).willReturn(id);

        given(cartRepository.findByMemberId(anyLong())).willReturn(cart);
        given(productService.findById(anyLong())).willReturn(product);
        given(cartItemRepository.findByCartIdAndProductId(any(), anyLong())).willReturn(null);

        //when
        CartItemRequest cartItemRequest = new CartItemRequest(1L, "L", "red", 3);
        RsData<CartItem> result = cartService.addCartItem(member, cartItemRequest);

        //then
        Assertions.assertThat(result.getResultCode()).isEqualTo("S-1");
        Assertions.assertThat(result.getMsg()).isEqualTo("상품이 추가되었습니다.");
        Assertions.assertThat(cart.getTotalCount()).isEqualTo(3);
    }

    @DisplayName("상품을 장바구니에 담는다. 이미 담긴 상품이라면 해당 상품수량이 증가한다. (장바구니에 해당 상품이 존재한다.)")
    @Test
    void addCartItem2() throws Exception {
        //given
        Long id = 1L; //공용

        Member member = mock(Member.class);
        Product product = mock(Product.class);
        Cart cart = new Cart(member); //카트는 완전히 비어 있는 상태

        CartItem cartItem = CartItem.builder().product(product).cart(cart).quantity(5).build();

        given(member.getId()).willReturn(id);
        given(product.getId()).willReturn(id);

        given(cartRepository.findByMemberId(anyLong())).willReturn(cart);
        given(productService.findById(anyLong())).willReturn(product);
        given(cartItemRepository.findByCartIdAndProductId(any(), anyLong())).willReturn(cartItem);


        //when
        CartItemRequest cartItemRequest = new CartItemRequest(1L, "L", "red", 3);
        RsData<CartItem> result = cartService.addCartItem(member, cartItemRequest);

        //then
        Assertions.assertThat(result.getResultCode()).isEqualTo("S-1");
        Assertions.assertThat(result.getMsg()).isEqualTo("상품이 추가되었습니다.");
        Assertions.assertThat(cartItem.getQuantity()).isEqualTo(8);
    }
    @DisplayName("장바구니에서 +, -로 수량을 업데이트한다.")
    @Test
    void updateCartItemCount() throws Exception {
        //given
        Long id = 1L; //공용

        Cart cart = new Cart(mock(Member.class));
        CartItem cartItem = CartItem.builder().cart(cart).quantity(10).build();
        given(cartItemRepository.findById(anyLong())).willReturn(Optional.of(cartItem));

        //when
        int quantity = 1;
        RsData result = cartService.updateCartItemCount(1L, quantity);

        //then
        Assertions.assertThat(result.getResultCode()).isEqualTo("S-1");
        Assertions.assertThat(result.getMsg()).isEqualTo("상품 수량이 업데이트 되었습니다.");
        Assertions.assertThat(cartItem.getQuantity()).isEqualTo(11);
    }

    @DisplayName("장바구니에서 선택된 상품들을 반환한다.")
    @Test
    void getSelectedCartItems() throws Exception {
        //given
        Long id = 1L; //공용

        Member member = mock(Member.class);
        Product product = mock(Product.class);
        Cart cart = new Cart(member); //카트는 완전히 비어 있는 상태

        given(member.getId()).willReturn(id);

        CartItem cartItem1 = CartItem.builder().product(product).cart(cart).quantity(5).build();
        CartItem cartItem2 = CartItem.builder().product(product).cart(cart).quantity(5).build();
        CartItem cartItem3 = CartItem.builder().product(product).cart(cart).quantity(5).build();

        given(cartItemRepository.findByCartIdAndProductId(any(), eq(1L))).willReturn((cartItem1));
        given(cartItemRepository.findByCartIdAndProductId(any(), eq(2L))).willReturn((cartItem2));
        given(cartService.getCartByUser(anyLong())).willReturn(cart);

        //when
        CartToOrderRequest request = new CartToOrderRequest(List.of(1L, 2L));
        List<CartItem> selectedCartItems = cartService.getSelectedCartItems(member, request);

        //then
        Assertions.assertThat(selectedCartItems.size()).isEqualTo(2);
    }
}