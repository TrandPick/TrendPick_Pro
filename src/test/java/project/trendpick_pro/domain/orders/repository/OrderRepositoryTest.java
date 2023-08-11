package project.trendpick_pro.domain.orders.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.IntegrationTestSupport;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.repository.BrandRepository;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.repository.MainCategoryRepository;
import project.trendpick_pro.domain.category.repository.SubCategoryRepository;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.ProductStatus;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static project.trendpick_pro.domain.orders.entity.OrderStatus.*;
import static project.trendpick_pro.domain.product.entity.product.ProductStatus.*;

@Transactional
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private MainCategoryRepository mainCategoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @AfterEach
    void tearDown() {
        orderItemRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAll();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 주문한 주문 목록을 조회한다.")
    @Test
    void findAllByMember() throws Exception {
        //given
        CommonFile commonFile = CommonFile.builder()
                .fileName("test.jpg")
                .build();
        ProductOption productOption = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();

        MainCategory mainCategory = mainCategoryRepository.save(new MainCategory("shirt"));
        SubCategory subCategory = subCategoryRepository.save(new SubCategory("long shirt", mainCategory));
        Brand brand = brandRepository.save( new Brand("Polo"));

        productOption.settingConnection(
                brand,
                mainCategory,
                subCategory,
                commonFile,
                SALE
        );

        Product product = Product.builder()
                .productCode("P" + UUID.randomUUID())
                .title("nice shirt title")
                .description("nice shirt description")
                .build();
        product.connectProductOption(productOption);
        Product savedProduct = productRepository.save(product);

        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        List<OrderItem> orderItems = List.of(
                OrderItem.of(savedProduct, 2, "L", "red"),
                OrderItem.of(savedProduct, 2, "XL", "blue")
        );

        //when
        Order order = Order.createOrder(savedMember, new Delivery("강남구 어디로"), orderItems);
        Order savedOrder = orderRepository.save(order);

        //when
        OrderSearchCond cond = new OrderSearchCond(savedMember.getId(), TEMP);
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderResponse> orders = orderRepository.findAllByMember(cond, pageable);

        //then
        assertThat(orders.getContent()).hasSize(2);
        assertThat(orders.getContent())
                .extracting("productName", "size", "color")
                .containsExactlyInAnyOrder(
                        tuple("nice shirt title", "L", "red"),
                        tuple("nice shirt title", "XL", "blue")
                );
        System.out.println("orders = " + orders);
    }

    @DisplayName("주문에 포함되는 주문 상품들을 조회한다.")
    @Test
    void findOrderItemsByOrderId() throws Exception {
        //given
        CommonFile commonFile = CommonFile.builder()
                .fileName("test.jpg")
                .build();
        ProductOption productOption = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();

        MainCategory mainCategory = mainCategoryRepository.save(new MainCategory("shirt"));
        SubCategory subCategory = subCategoryRepository.save(new SubCategory("long shirt", mainCategory));
        Brand brand = brandRepository.save( new Brand("Polo"));

        productOption.settingConnection(
                brand,
                mainCategory,
                subCategory,
                commonFile,
                SALE
        );

        Product product1 = Product.builder()
                .productCode("P" + UUID.randomUUID())
                .title("product1 title")
                .description("product1 description")
                .build();

        Product product2 = Product.builder()
                .productCode("P" + UUID.randomUUID())
                .title("product2 title")
                .description("product2 description")
                .build();

        Product product3 = Product.builder()
                .productCode("P" + UUID.randomUUID())
                .title("product3 title")
                .description("product3 description")
                .build();

        product1.connectProductOption(productOption);
        product2.connectProductOption(productOption);
        product3.connectProductOption(productOption);

        List<Product> products = productRepository.saveAll(List.of(product1, product2, product3));

        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        List<OrderItem> orderItems1 = List.of(
                OrderItem.of(products.get(0), 2, "L", "red"),
                OrderItem.of(products.get(0), 2, "XL", "blue")
        );

        List<OrderItem> orderItems2 = List.of(
                OrderItem.of(products.get(1), 2, "L", "red"),
                OrderItem.of(products.get(2), 2, "XL", "blue")
        );

        Order order1 = Order.createOrder(savedMember, new Delivery("강남구 어디로"), orderItems1);
        Order order2 = Order.createOrder(savedMember, new Delivery("강남구 어디로"), orderItems2);
        List<Order> orders = orderRepository.saveAll(List.of(order1, order2));

        //when
        List<OrderResponse> findOrder1 = orderRepository.findOrderItemsByOrderId(orders.get(0).getId());
        List<OrderResponse> findOrder2 = orderRepository.findOrderItemsByOrderId(orders.get(1).getId());

        //then
        assertThat(findOrder1).hasSize(2)
                .extracting("productId", "productName", "size", "color")
                .containsExactlyInAnyOrder(
                        tuple(products.get(0).getId(), "product1 title", "L", "red"),
                        tuple(products.get(0).getId(), "product1 title", "XL", "blue")
                );
        assertThat(findOrder2).hasSize(2)
                .extracting("productId", "productName", "size", "color")
                .containsExactlyInAnyOrder(
                        tuple(products.get(1).getId(), "product2 title", "L", "red"),
                        tuple(products.get(2).getId(), "product3 title", "XL", "blue")
                );
    }

    @DisplayName("브랜드 관리자가 자신의 브랜드 상품이 주문된 주문목록을 조회한다.")
    @Test
    void findAllBySeller() throws Exception {
        //given
        CommonFile commonFile = CommonFile.builder()
                .fileName("test.jpg")
                .build();

        MainCategory mainCategory = mainCategoryRepository.save(new MainCategory("shirt"));
        SubCategory subCategory = subCategoryRepository.save(new SubCategory("long shirt", mainCategory));
        Brand polo = brandRepository.save( new Brand("Polo"));
        Brand nike = brandRepository.save( new Brand("Nike"));

        ProductOption productOption1 = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();

        productOption1.settingConnection(
                polo,
                mainCategory,
                subCategory,
                commonFile,
                SALE
        );

        ProductOption productOption2 = ProductOption.builder()
                .size(List.of("L", "XL"))
                .color(List.of("red", "blue"))
                .stock(10)
                .price(10000)
                .build();

        productOption2.settingConnection(
                nike,
                mainCategory,
                subCategory,
                commonFile,
                SALE
        );

        Product product1 = Product.builder()
                .productCode("P" + UUID.randomUUID())
                .title("product1 title")
                .description("product1 description")
                .build();

        Product product2 = Product.builder()
                .productCode("P" + UUID.randomUUID())
                .title("product2 title")
                .description("product2 description")
                .build();

        Product product3 = Product.builder()
                .productCode("P" + UUID.randomUUID())
                .title("product3 title")
                .description("product3 description")
                .build();

        product1.connectProductOption(productOption1);
        product2.connectProductOption(productOption2);
        product3.connectProductOption(productOption2);

        List<Product> products = productRepository.saveAll(List.of(product1, product2, product3));

        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        List<OrderItem> orderItems1 = List.of(
                OrderItem.of(products.get(0), 2, "L", "red"),
                OrderItem.of(products.get(0), 2, "XL", "blue")
        );

        List<OrderItem> orderItems2 = List.of(
                OrderItem.of(products.get(1), 2, "L", "red"),
                OrderItem.of(products.get(2), 2, "XL", "blue")
        );

        Order order1 = Order.createOrder(savedMember, new Delivery("강남구 어디로"), orderItems1);
        Order order2 = Order.createOrder(savedMember, new Delivery("강남구 어디로"), orderItems2);
        orderRepository.saveAll(List.of(order1, order2));

        //when
        OrderSearchCond cond = new OrderSearchCond(polo.getId());
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderResponse> orders = orderRepository.findAllBySeller(cond, pageable);

        //then
        assertThat(orders).hasSize(4)
                .extracting("productId", "productName", "size", "color")
                .containsExactlyInAnyOrder(
                        tuple(products.get(0).getId(), "product1 title", "L", "red"),
                        tuple(products.get(0).getId(), "product1 title", "XL", "blue"),
                        tuple(products.get(1).getId(), "product2 title", "L", "red"),
                        tuple(products.get(2).getId(), "product3 title", "XL", "blue")
                );
    }
}