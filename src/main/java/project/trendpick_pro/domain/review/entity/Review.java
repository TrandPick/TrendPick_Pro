package project.trendpick_pro.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {
    @Id @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String writer;

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private CommonFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private int rating;

    public void disconnectFile() {
        this.file = null;
    }

    @Builder
    private Review(String writer, Product product, CommonFile file, String title, String content, int rating){
        this.writer = writer;
        this.product = product;
        this.file = file;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public static Review of(ReviewSaveRequest reviewSaveRequest, Member member, Product product, CommonFile file) {
        return Review.builder()
                .writer(member.getUsername())
                .product(product)
                .file(file)
                .title(reviewSaveRequest.title())
                .content(reviewSaveRequest.content())
                .rating(reviewSaveRequest.rating())
                .build();
    }

    public void update(ReviewSaveRequest reviewSaveRequest, CommonFile file) {
        this.title = reviewSaveRequest.title();
        this.content = reviewSaveRequest.content();
        this.rating = reviewSaveRequest.rating();
        this.file = file;
    }
}