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

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private CommonFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //Product

    private String title;
    @Lob
    private String content;

    private int rating;

    @Builder
    public Review(Member member, Product product, CommonFile file, String title, String content, int rating){
        this.writer = member.getUsername();
        this.product = product;
        this.file = file;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public static Review of(ReviewCreateRequest reviewCreateRequest, Member member, Product product, CommonFile file) {
        return Review.builder()
                .writer(member.getUsername())
                .product(product)
                .file(file)
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

//    public void changeMainFile(String filePath, String mainFilePath, MultipartFile mainFile) throws IOException {
//        File file = new File(filePath + mainFileName);
//        file.delete();  //원래 있던거 삭제
//
//        this.mainFileName = mainFilePath;
//        mainFile.transferTo(new File(filePath + mainFilePath));
//    }
//
//    public void changeSubFile(String filePath, List<String> subFileNames) {
//        //음 우선 돌면서 지워야겠지??
//        for(String subFile: subFileNames){
//            File file = new File(filePath + subFile);
//            file.delete();
//        }
//        this.subFileNames = subFileNames;
//    }
}
