package project.trendpick_pro.domain.orders.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static project.trendpick_pro.domain.cart.entity.Cart.createCart;

class OrderItemTest {

    @DisplayName("직접 상품 주문 시에 지정한 재고 만큼 상품의 재고를 줄인다.")
    @Test
    void createOrderItemByProduct() throws Exception {
        //given
        Product product = Product.builder()
                .title("nice shirt title")
                .description("nice shirt description")
                .build();

        ProductOption productOption = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();
        product.connectProductOption(productOption);

        //when
        OrderItem orderItem = OrderItem.of(product, 2, "L", "red");

        //then\
        assertThat(productOption.getStock()).isEqualTo(8);
        assertThat(orderItem)
                .extracting("size", "quantity", "color")
                .contains("L", 2, "red");
    }

    @DisplayName("직접 상품 주문 시에 지정한 재고보다 상품의 재고가 적으면 예외가 발생한다.")
    @Test
    void createOrderItemByProductOverStock() throws Exception {
        //given
        Product product = Product.builder()
                .productCode(UUID.randomUUID().toString())
                .title("nice shirt title")
                .description("nice shirt description")
                .build();

        ProductOption productOption = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();
        product.connectProductOption(productOption);

        //when //then
        assertThatThrownBy(() -> OrderItem.of(product, 11, "L", "red"))
                .isInstanceOf(ProductStockOutException.class)
                .hasMessage("상품의 재고가 부족하여 주문이 불가능합니다.");
    }

    @DisplayName("장바구니 주문 시에 지정한 재고 만큼 상품의 재고를 줄인다.")
    @Test
    void createOrderItemByCart() throws Exception {
        //given
        Product product = Product.builder()
                .title("nice shirt title")
                .description("nice shirt description")
                .build();

        ProductOption productOption = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();
        product.connectProductOption(productOption);

        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();

        Cart cart = createCart(member);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(4)
                .size("L")
                .color("red")
                .build();

        //when
        OrderItem orderItem = OrderItem.of(product, cartItem);

        //then
        assertThat(productOption.getStock()).isEqualTo(6);
        assertThat(orderItem)
                .extracting("size", "quantity", "color")
                .contains("L", 4, "red");
    }

    @DisplayName("장바구니 주문 시에 지정한 재고보다 상품의 재고가 적으면 예외가 발생한다.")
    @Test
    void createOrderItemByCartOverStock() throws Exception {
        //given
        Product product = Product.builder()
                .productCode(UUID.randomUUID().toString())
                .title("nice shirt title")
                .description("nice shirt description")
                .build();

        ProductOption productOption = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();
        product.connectProductOption(productOption);

        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();

        Cart cart = createCart(member);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(20)
                .size("L")
                .color("red")
                .build();

        //when //then
        assertThatThrownBy(() -> OrderItem.of(product, cartItem))
                .isInstanceOf(ProductStockOutException.class)
                .hasMessage("상품의 재고가 부족하여 주문이 불가능합니다.");
    }

    @DisplayName("주문이 취소되면 기존 상품의 재고가 주문수량만큼 증가한다.")
    @Test
    void cancelOrderAfterIncreaseStock() throws Exception {
        //given
        Product product = Product.builder()
                .title("nice shirt title")
                .description("nice shirt description")
                .build();

        ProductOption productOption = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();
        product.connectProductOption(productOption);

        OrderItem orderItem = OrderItem.of(product, 2, "L", "red");

        //when
        orderItem.cancel();

        //then
        assertThat(productOption.getStock()).isEqualTo(10);
    }
}