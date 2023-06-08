package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.trendpick_pro.domain.member.entity.Member;


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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // 총 수량 필드
    private int totalCount;

    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.member= member;
        cart.totalCount = 0;
        return cart;
    }

    public Cart(Member member) {
        this.member = member;
    }

    // CartItem 추가
    public void addItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
       // updateTotalCountAndPrice();
    }

    // CartItem 삭제
    public void removeItem(Long cartItemId) {
        CartItem cartItem = findCartItemById(cartItemId);
        if (cartItem != null) {
            cartItems.remove(cartItem);
        }
    }


    // CartItem 수량 변경
    public void updateItemCount(Long cartItemId, int quantity) {
        CartItem cartItem = findCartItemById(cartItemId);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
          //  updateTotalCountAndPrice();
        }
    }

    private CartItem findCartItemById(Long cartItemId) {
        return cartItems.stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElse(null);
    }

}