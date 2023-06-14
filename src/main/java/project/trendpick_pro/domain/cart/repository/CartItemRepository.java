package project.trendpick_pro.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.cart.entity.CartItem;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndProductId(Long cartId, long ProductId);

    List<CartItem> findByProductId(Long productId);
    CartItem findByCartId(Long cartItemID);
}
