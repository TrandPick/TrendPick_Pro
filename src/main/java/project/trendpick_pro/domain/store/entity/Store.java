package project.trendpick_pro.domain.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    //    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "brand")
    @Column(unique = true)
    private String brand; //임시

    public Store(String brand){
        this.brand = brand;
    }
}
