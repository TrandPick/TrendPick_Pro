package project.trendpick_pro.domain.cart.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.entity.dto.request.CartItemRequest;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CartItemRepositoryTest {
    @Autowired
    protected CartItemRepository cartItemRepository;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected CartRepository cartRepository;
    @Autowired
    protected MemberRepository memberRepository;

    @BeforeEach
    void before(){

    }

    @Test
    @DisplayName("장바구니 id와 상품 id를 통해 해당 장바구니에 들어있는 상품(CartItem)을 찾는다.")
    void findByCartIdAndProductId() throws Exception{
        //카트생성
        Cart cart = cartRepository.save(new Cart(createMember()));//cartId : 1L

        //상품생성
        Product product1 = productRepository.save(Product.of("상품1", "상품입니다."));//productId : 1L
        Product product2 = productRepository.save(Product.of("상품2", "상품입니다."));//productId : 2L

        //카트아이템 생성 (1번 카트와 1번 상품)
        CartItemRequest cartItemRequest1 = new CartItemRequest(product1.getId(), "S", "red", 3);
        CartItem cartItem1 = CartItem.of(cart, product1, cartItemRequest1);
        cartItemRepository.save(cartItem1);

        //카트아이템 생성 (1번 카트와 2번 상품)
        CartItemRequest cartItemRequest2 = new CartItemRequest(product2.getId(), "L", "blue", 3);
        CartItem cartItem2 = CartItem.of(cart, product2, cartItemRequest2);
        cartItemRepository.save(cartItem2);

        //1번 카트와 1번 상품
        CartItem findCartItem1 = cartItemRepository.findByCartIdAndProductId(cart.getId(), product1.getId());
        //1번 카트와 2번 상품
        CartItem findCartItem2 = cartItemRepository.findByCartIdAndProductId(cart.getId(), product2.getId());

        assertThat(findCartItem1).isNotNull();
        assertThat(findCartItem1.getId()).isEqualTo(cartItem1.getId());

        assertThat(findCartItem2).isNotNull();
        assertThat(findCartItem2.getId()).isEqualTo(cartItem2.getId());
    }

    @Test
    @DisplayName("장바구니에 있는 모든 cartItem을 가져온다.")
    void findAllByCart() throws Exception{
        //카트생성
        Cart cart = cartRepository.save(new Cart(createMember())); //cartId : 1L

        //카트아이템 생성 (10개)
        for(int i=0; i<10; i++){
            CartItemRequest cartItemRequest1 = new CartItemRequest(null, "S", "red", 3);
            CartItem cartItem1 = CartItem.of(cart, null, cartItemRequest1);
            cartItemRepository.save(cartItem1);
        }

        List<CartItem> allByCart = cartItemRepository.findAllByCart(cart);
        assertThat(allByCart.size()).isEqualTo(10);
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
        return memberRepository.save(member);
    }
}