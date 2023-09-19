package project.trendpick_pro.domain.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand", unique = true)
    private String brand;

    //정산 데이터 생성 -> 정산처리 -> 캐시 처리
    @Column(name = "rebate_cash")
    private int rebatedCash;

    public Store(String brand){
        this.brand = brand;
    }

    public void addRebatedCash(long price) {
        this.rebatedCash += price;
    }

    public void withdrawRebatedCash(int price){
        if(this.rebatedCash <= price)
            throw new IllegalArgumentException("출금 요청한 금액이 정산 금액보다 많습니다.");
        this.rebatedCash -= price;
    }
}
