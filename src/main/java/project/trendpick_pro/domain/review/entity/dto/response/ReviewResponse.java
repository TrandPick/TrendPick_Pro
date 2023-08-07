package project.trendpick_pro.domain.review.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.review.entity.Review;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {
    private Long id;
    private String writer;
    private String productName;
    private String mainFile;
    private List<String> subFiles;
    private String title;

    private String content;

    private int rating;

    @Builder
    @QueryProjection
    public ReviewResponse(Long id, String writer, String productName, String title, String content,
                          String mainFile, List<String> subFiles, int rating) {
        this.id = id;
        this.writer = writer;
        this.productName = productName;
        this.title = title;
        this.content = content;
        this.mainFile = mainFile;
        this.subFiles = subFiles;
        this.rating = rating;
    }

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .writer(review.getWriter())
                .productName(review.getProduct().getTitle())
                .title(review.getTitle())
                .content(review.getContent())
                .mainFile(review.getFile().getFileName())
                .subFiles(subFiles(review.getFile().getChild()))
                .rating(review.getRating())
                .build();
    }
    public static ReviewResponse of(String msg) {
        return ReviewResponse.builder()
                .content(msg)
                .build();
    }

    private static List<String> subFiles(List<CommonFile> subFiles) {
        List<String> tmpList = new ArrayList<>();

        for (CommonFile subFile : subFiles) {
            tmpList.add(subFile.getFileName());
        }
        return tmpList;
    }
}
