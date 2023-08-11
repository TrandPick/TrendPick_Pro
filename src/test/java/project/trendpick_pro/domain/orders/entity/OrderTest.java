package project.trendpick_pro.domain.orders.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class OrderTest {

    @DisplayName("주문 생성시에 주문의 상태는 TEMP로 생성된다.")
    @Test
    void createOrder() throws Exception {
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
        List<OrderItem> orderItems = List.of(
                OrderItem.of(product, 2, "L", "red"),
                OrderItem.of(product, 2, "XL", "blue")
        );

        //when
        Order order = Order.createOrder(member, new Delivery("강남구 어디로"), orderItems);

        //then
        assertThat(order)
                .extracting("orderStatus")
                .isEqualTo(OrderStatus.TEMP);
    }

    @DisplayName("주문 생성 시 상품 리스트에서 주문의 총 금액을 계산한다.")
    @Test
    void calculateTotalPrice() throws Exception {
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
        List<OrderItem> orderItems = List.of(
                OrderItem.of(product, 2, "L", "red"),
                OrderItem.of(product, 2, "XL", "blue")
        );

        //when
        Order order = Order.createOrder(member, new Delivery("강남구 어디로"), orderItems);

        //then
        assertThat(order.getTotalPrice()).isEqualTo(40000);
        assertThat(order.getOrderItems())
                .extracting("size", "quantity", "color")
                .containsExactly(
                        tuple("L", 2, "red"),
                        tuple("XL", 2, "blue")
                );
    }

    @DisplayName("주문이 취소되면 주문의 상태값은 CANCELED로 수정된다.")
    @Test
    void cancelAndUpdateStatus() throws Exception {
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
        List<OrderItem> orderItems = List.of(
                OrderItem.of(product, 2, "L", "red"),
                OrderItem.of(product, 2, "XL", "blue")
        );

        Order order = Order.createOrder(member, new Delivery("강남구 어디로"), orderItems);

        //when
        order.cancel();

        //then
        assertThat(order)
                .extracting("orderStatus")
                .isEqualTo(OrderStatus.CANCELED);
    }

    @DisplayName("주문이 취소되면 기존 상품의 재고를 원상복구 시킨다.")
    @Test
    void cancelAndStockRestoration() throws Exception {
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
        List<OrderItem> orderItems = List.of(
                OrderItem.of(product, 2, "L", "red"),
                OrderItem.of(product, 2, "XL", "blue")
        );

        Order order = Order.createOrder(member, new Delivery("강남구 어디로"), orderItems);

        //when
        order.cancel();

        //then
        assertThat(productOption.getStock()).isEqualTo(10);
    }
}