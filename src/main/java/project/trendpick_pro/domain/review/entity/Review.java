package project.trendpick_pro.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewCreateRequest;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewUpdateRequest;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Review extends BaseTimeEntity {
    @Id @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member writer;

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "common_file_id")
    private CommonFile commonFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //Product

    private String title;
    @Lob
    private String content;

    private int rating;

    @Builder
    public Review(Member member, Product product, CommonFile commonFile, String title, String content, int rating){
        this.writer = member;
        this.product = product;
        this.commonFile = commonFile;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public static Review of(ReviewCreateRequest reviewCreateRequest, Member member, Product product) {
        return Review.builder()
                .writer(member)
                .product(product)
                //.commonFile(reviewRequest.getMainFile());
                .content(reviewCreateRequest.getContent())
                .rating(reviewCreateRequest.getRating())
                .build();
    }

    public void update(ReviewUpdateRequest reviewUpdateRequest){
        this.title = reviewUpdateRequest.getTitle();
        this.content = reviewUpdateRequest.getContent();
        //this.commonFile = reviewUpdateRequest.getMainFile();
        this.rating = reviewUpdateRequest.getRating();
    }
}
