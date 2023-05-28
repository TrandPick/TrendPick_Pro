package project.trendpick_pro.domain.cart.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    private int totalCount; // 총 수량

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "user_id")
    private String user_id;   // user

    private int totalPrice; // 총 금액
}
