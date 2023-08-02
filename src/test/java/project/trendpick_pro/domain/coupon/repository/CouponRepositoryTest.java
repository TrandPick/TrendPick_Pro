package project.trendpick_pro.domain.coupon.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.IntegrationTestSupport;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
class CouponRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private StoreRepository storeRepository;

    @AfterEach
    void tearDown() {
        couponRepository.deleteAllInBatch();
        storeRepository.deleteAllInBatch();
    }

    @DisplayName("브랜드 이름으로 쿠폰을 조회한다.")
    @Test
    void findAllByBrand() throws Exception {
        //given
        Store store1 = new Store("polo");
        Store store2 = new Store("nike");
        Store store3 = new Store("adidas");
        storeRepository.saveAll(List.of(store1, store2, store3));

        Coupon coupon1 = createCoupon("polo");
        coupon1.connectStore(store1);
        Coupon coupon2 = createCoupon("polo");
        coupon2.connectStore(store1);
        Coupon coupon3 = createCoupon("nike");
        coupon3.connectStore(store2);
        Coupon coupon4 = createCoupon("nike");
        coupon4.connectStore(store2);
        Coupon coupon5 = createCoupon("adidas");
        coupon5.connectStore(store3);
        couponRepository.saveAll(List.of(coupon1, coupon2, coupon3, coupon4, coupon5));

        //when
        List<Coupon> coupons = couponRepository.findAllByBrand("nike");

        //then
        assertThat(coupons).hasSize(2)
                .extracting("id", "store.brand")
                .containsExactlyInAnyOrder(
                        tuple(coupons.get(0).getId(), "nike"),
                        tuple(coupons.get(1).getId(), "nike")
                );
    }

    private static Coupon createCoupon(String storeName) {
         Coupon coupon = Coupon.builder()
                .name("couponName")
                .limitCount(100)
                .limitIssueDate(10)
                .minimumPurchaseAmount(1000)
                .discountPercent(10)
                .build();
         coupon.assignPostIssueExpiration(10);
         return coupon;
    }
}