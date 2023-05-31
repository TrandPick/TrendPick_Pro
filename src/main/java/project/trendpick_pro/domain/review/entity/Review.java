package project.trendpick_pro.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewCreateRequest;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewUpdateRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Review extends BaseTimeEntity {
    @Id @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writer;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "review_image_id")
    private ReviewImage reviewImage;

//    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
//    @JoinColumn(name = "common_file_id")
//    private CommonFile commonFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //Product

    private String title;
    @Lob
    private String content;

    private int rating;

    @Builder
    public Review(Member member, Product product, ReviewCreateRequest reviewCreateRequest){
        this.writer = member.getUsername();
        this.product = product;
        this.title = reviewCreateRequest.getTitle();
        this.content = reviewCreateRequest.getContent();
        this.rating = reviewCreateRequest.getRating();
    }

    public static Review of(ReviewCreateRequest reviewCreateRequest, Member member, Product product) {
        return Review.builder()
                .writer(member.getUsername())
                .product(product)
                //.commonFile(makeFile(reviewCreateRequest.getMainFile(), reviewCreateRequest.getSubFiles()));
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

    public void matchReviewImage(ReviewImage reviewImage){
        this.reviewImage = reviewImage;
    }

//    @Value("${file.dir}")
//    private static String filePath;
//
//    private static String createStoreFileName(String originFileName){
//        String ext = extractExt(originFileName);
//        String uuid = UUID.randomUUID().toString();
//        return uuid + "." + ext;
//    }
//
//    private static String extractExt(String originFileName) {
//        int pos = originFileName.lastIndexOf(".");
//        return originFileName.substring(pos + 1);
//    }

//    private static CommonFile makeFile(MultipartFile mainFile, List<MultipartFile> subFiles) throws IOException {
//        String mainFileName = createStoreFileName(mainFile.getOriginalFilename());
//        List<String> subFileName = new ArrayList<>();
//
//        mainFile.transferTo(new File(filePath + mainFileName));
//
//        for(MultipartFile subFile : subFiles){
//            String savePath = createStoreFileName(subFile.getOriginalFilename());
//            subFileName.add(savePath);
//            subFile.transferTo(new File(filePath + savePath));
//        }
//
//        return null;
//
//
//    }
}
