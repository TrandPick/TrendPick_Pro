package project.trendpick_pro.domain.cart.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
@DataJpaTest
class CartRepositoryTest {

    @Autowired
    protected CartRepository cartRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Test
    @DisplayName("해당 회원의 장바구니를 조회한다.")
    void findByMemberId() throws Exception {
        Member member = memberRepository.save(Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build());

        Cart cart = cartRepository.save(new Cart(member));

        Cart findCart = cartRepository.findByMemberId(member.getId());

        Assertions.assertThat(findCart.getId()).isEqualTo(cart.getId());
    }

}