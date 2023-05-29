package project.trendpick_pro.domain.cart.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.product.entity.ProductOption;
import project.trendpick_pro.domain.product.repository.ProductOptionRepository;


@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductOptionRepository productOptionRepository;

    public CartService(CartRepository cartRepository, ProductOptionRepository productOptionRepository) {
        this.cartRepository = cartRepository;
        this.productOptionRepository = productOptionRepository;
    }

    public Cart createCart(User user) {
        Cart cart = new Cart(user);
        return cartRepository.save(cart);
    }

    public void addItemToCart(User user, Long productOptionId, int count) {
        Cart cart = getCartByUser(user);
        ProductOption productOption = getProductOptionById(productOptionId);

        CartItem cartItem = cart.findCartItemByProductOption(productOption);


        if (cartItem != null) {
            // 이미 카트에 해당 상품이 존재하는 경우, 수량을 증가
            cartItem.setCount(cartItem.getCount() + count);
        } else {
            // 카트에 해당 상품이 없는 경우, 새로운 카트 아이템을 생성하여 추가
            cartItem = new CartItem(cart, productOption, count);
            cart.addItem(cartItem);
        }
    }

    // 상품을 장바구니에서 제거
    public void removeItemFromCart(User user, Long cartItemId) {
        Cart cart = getCartByUser(user);
        cart.removeItem(cartItemId);
    }

    // 상품의 수량 업데이트
    public void updateItemCount(User user, Long cartItemId, int count) {
        Cart cart = getCartByUser(user);
        cart.updateItemCount(cartItemId, count);
    }

    public Cart getCartByUser(User user) {
       // return cartRepository.findByUser(user);
    }

    private ProductOption getProductOptionById(Long productOptionId) {
       // return productOptionRepository.findById(productOptionId);
    }
}
