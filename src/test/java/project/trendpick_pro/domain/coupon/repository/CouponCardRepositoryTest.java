package project.trendpick_pro.domain.coupon.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

//@Transactional
@DataJpaTest
class CouponCardRepositoryTest {

    @Autowired
    private CouponCardRepository couponCardRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoreRepository storeRepository;

    @AfterEach
    void tearDown() {
        couponCardRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        storeRepository.deleteAllInBatch();
    }

    @DisplayName("소지한 쿠폰의 개수를 조회한다.")
    @Test
    void countByCouponIdAndMemberId() throws Exception {
        //given
        LocalDateTime registeredDateTime = LocalDateTime.of(2023, 8, 1, 10, 10, 10);

        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        Store store = new Store("polo");
        storeRepository.save(store);

        Coupon coupon = createCoupon("polo");
        coupon.connectStore(store);
        couponRepository.save(coupon);

        CouponCard couponCard1 = new CouponCard(coupon);
        CouponCard couponCard2 = new CouponCard(coupon);
        couponCard1.connectMember(savedMember);
        couponCard1.updatePeriod(registeredDateTime);
        couponCard2.connectMember(savedMember);
        couponCard2.updatePeriod(registeredDateTime);
        couponCardRepository.saveAll(List.of(couponCard1, couponCard2));

        //when
        int count = couponCardRepository.countByCouponIdAndMemberId(coupon.getId(), savedMember.getId());

        //then
        assertThat(count).isEqualTo(2);
    }

    @DisplayName("임의로 지정한 브랜드명의 쿠폰들을 조회한다.")
    @Test
    void findAllByBrand() throws Exception {
        //given
        LocalDateTime registeredDateTime = LocalDateTime.of(2023, 8, 1, 10, 10, 10);

        Member member = Member.builder()
                .email("TrendPick@email.com")
                .password("12345")
                .username("TrendPick")
                .phoneNumber("010-1234-5678")
                .role(MemberRoleType.MEMBER)
                .brand("Polo")
                .build();
        Member savedMember = memberRepository.save(member);

        Store store1 = new Store("polo");
        Store store2 = new Store("nike");
        storeRepository.saveAll(List.of(store1, store2));

        Coupon coupon1 = createCoupon("polo");
        Coupon coupon2 = createCoupon("polo");
        Coupon coupon3 = createCoupon("nike");
        coupon1.connectStore(store1);
        coupon2.connectStore(store1);
        coupon3.connectStore(store2);
        couponRepository.saveAll(List.of(coupon1, coupon2, coupon3));

        CouponCard couponCard1 = new CouponCard(coupon1);
        CouponCard couponCard2 = new CouponCard(coupon2);
        CouponCard couponCard3 = new CouponCard(coupon3);
        CouponCard couponCard4 = new CouponCard(coupon3);
        couponCard1.connectMember(savedMember);
        couponCard1.updatePeriod(registeredDateTime);
        couponCard2.connectMember(savedMember);
        couponCard2.updatePeriod(registeredDateTime);
        couponCard3.connectMember(savedMember);
        couponCard3.updatePeriod(registeredDateTime);
        couponCard4.connectMember(savedMember);
        couponCard4.updatePeriod(registeredDateTime);
        couponCardRepository.saveAll(List.of(couponCard1, couponCard2, couponCard3, couponCard4));

        //when
        List<CouponCard> couponCards = couponCardRepository.findAllByBrand("nike");

        //then
        assertThat(couponCards).hasSize(2)
                .extracting("id", "coupon.store.brand")
                .containsExactlyInAnyOrder(
                        tuple(couponCards.get(0).getId(), "nike"),
                        tuple(couponCards.get(1).getId(), "nike")
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