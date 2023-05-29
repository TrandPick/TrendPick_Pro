package project.trendpick_pro.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
