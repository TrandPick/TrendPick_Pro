package project.trendpick_pro.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.member.entity.Member;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByMemberId(Long memberId);
}
