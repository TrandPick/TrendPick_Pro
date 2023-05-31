package project.trendpick_pro.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByMemberId(Long memberId);
    List<Cart> findByCartMemberId(Member member);
}

