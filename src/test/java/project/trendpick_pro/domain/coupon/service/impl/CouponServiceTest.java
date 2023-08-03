package project.trendpick_pro.domain.coupon.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import project.trendpick_pro.IntegrationTestSupport;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponResponse;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.coupon.service.CouponService;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class CouponServiceTest extends IntegrationTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        couponRepository.deleteAllInBatch();
        storeRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @DisplayName("브랜드에서 쿠폰을 발급한다.")
    @Test
    void createCoupon() throws Exception {
        //given
        String storeName = "polo";
        storeRepository.save(new Store(storeName));

        StoreCouponSaveRequest request = StoreCouponSaveRequest.builder()
                .name("coupon")
                .limitCount(100)
                .limitIssueDate(10)
                .minimumPurchaseAmount(5000)
                .discountPercent(50)
                .expirationType("ISSUE_AFTER_DATE")
                .issueAfterDate(21)
                .build();

        //when
        RsData<String> couponId = couponService.createCoupon(storeName, request);

        //then
        Coupon coupon = couponRepository.findById(Long.parseLong(couponId.getData())).get();
        assertThat(coupon).isNotNull()
                .extracting("name", "limitIssueDate", "minimumPurchaseAmount", "discountPercent")
                .contains("[polo] coupon", 10, 5000, 50);
    }

    @DisplayName("모든 브랜드별 쿠폰들을 조회한다.")
    @Test
    void findAllCoupons() throws Exception {
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
        List<CouponResponse> coupons = couponService.findAllCoupons();

        //then
        assertThat(coupons).hasSize(5)
                .extracting("couponName", "minimumPurchaseAmount", "limitCount", "issueCount")
                .contains(
                        tuple("[polo] couponName", 1000, 100, 0),
                        tuple("[polo] couponName", 1000, 100, 0),
                        tuple("[nike] couponName", 1000, 100, 0),
                        tuple("[nike] couponName", 1000, 100, 0),
                        tuple("[adidas] couponName", 1000, 100, 0)
                );
    }

    private static Coupon createCoupon(String storeName) {
        Coupon coupon = Coupon.builder()
                .name("[" + storeName + "]" + " " + "couponName")
                .limitCount(100)
                .limitIssueDate(10)
                .minimumPurchaseAmount(1000)
                .discountPercent(10)
                .build();
        coupon.assignPostIssueExpiration(10);
        return coupon;
    }
}