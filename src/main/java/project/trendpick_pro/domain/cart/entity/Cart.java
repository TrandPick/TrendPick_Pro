package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;
import project.trendpick_pro.domain.product.entity.ProductOption;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    //@OneToOne(fetch = FetchType.LAZY)
   // @JoinColumn(name = "user_id")
     // private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // 총 수량 필드
    private int totalCount;

    // 총 가격 필드
    private int totalPrice;

    public Cart(User user) {
        //this.user = user;
    }

    // CartItem 추가
    public void addItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
        updateTotalCountAndPrice();
    }

    // CartItem 삭제
    public void removeItem(Long cartItemId) {
        CartItem cartItem = findCartItemById(cartItemId);
        if (cartItem != null) {
            cartItems.remove(cartItem);
            updateTotalCountAndPrice();
        }
    }


    // CartItem 수량 변경
    public void updateItemCount(Long cartItemId, int count) {
        CartItem cartItem = findCartItemById(cartItemId);
        if (cartItem != null) {
            cartItem.setCount(count);
            updateTotalCountAndPrice();
        }
    }

    private CartItem findCartItemById(Long cartItemId) {
        return cartItems.stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElse(null);
    }

    private void updateTotalCountAndPrice() {
        totalCount = cartItems.stream()
                .mapToInt(CartItem::getCount)
                .sum();

        totalPrice = cartItems.stream()
                .mapToInt(CartItem::getPrice)
                .sum();
    }

    public CartItem findCartItemByProductOption(ProductOption productOption) {
        return cartItems.stream()
                .filter(item -> item.getProductOption().equals(productOption))
                .findFirst()
                .orElse(null);
    }
}