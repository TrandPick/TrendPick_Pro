package project.trendpick_pro.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.user.entity.User;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@ManyToOne
    //@JoinColumn(name = "user_id")
    private Long user_id;   //User

//    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "product_id")
    private Long product_id;    //Product

    @Lob
    private String content;

    private int rating;
}
