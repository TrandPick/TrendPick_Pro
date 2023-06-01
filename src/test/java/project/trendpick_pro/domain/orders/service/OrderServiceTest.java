package project.trendpick_pro.domain.orders.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSaveRequest;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OrderServiceTest.class);


    @InjectMocks
    OrderService orderService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 이 부분에서 테스트에 필요한 데이터를 설정하실 수 있습니다.
//        Member member = new Member();
//        Product product = new Product(); // 적절한 Product 객체 생성
//        product.setStock(10);
//        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
//        when(productRepository.findById(any())).thenReturn(Optional.of(product));
    }

    @Test
    @DisplayName("주문 재고 동시성 확인")
    public void concurrencyTest() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 1; i <= 11; i++) {
            int finalI = i;
            Future<?> future = executorService.submit(() -> {
                try {
                    orderService.order(1L, new OrderSaveRequest(1L, 1)); // 적절한 OrderSaveRequest 객체 사용
                    logger.info(String.valueOf(finalI));
                } catch (Exception e) {
                    logger.error(String.valueOf(finalI));
                    throw new IllegalStateException(e);
                }
            });
            futures.add(future);
        }

        for (Future<?> future : futures) {
            future.get();
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    }

    private long account = 100_000L; // 계좌 잔고 100,000원

    @Test
    void pay() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(2);

        // when
        executor.submit(() -> execute("A", 40_000L, countDownLatch)); // A가 40,000원 결제
        executor.submit(() -> execute("B", 20_000L, countDownLatch)); // B가 20,000원 결제
        countDownLatch.await();

        // then
        assertThat(account).isEqualTo(40_000L); // 60,000원을 사용했으므로 예상 잔액은 40,000원
    }

    private void execute(String username, long money, CountDownLatch countDownLatch) {
        try {
            pay(username, money);
        } finally {
            countDownLatch.countDown();
        }
    }

    private synchronized void pay(String username, long money) {
        long result = account - money;
        System.out.printf("%s가 %d원을 사용한 뒤 남은 금액은 %d 입니다.\n", username, money, result);
        account = result;
    }
}
