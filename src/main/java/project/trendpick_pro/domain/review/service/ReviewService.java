package project.trendpick_pro.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.common.base.filetranslator.FileTranslator;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.repository.ReviewImageRepository;
import project.trendpick_pro.domain.review.repository.ReviewRepository;

import java.io.IOException;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final FileTranslator fileTranslator;

    @Value("${file.dir}")
    private static String filePath;


    public ReviewResponse showReview(Long productId) {
        Review review = reviewRepository.findById(productId).orElseThrow();

        return ReviewResponse.of(review);
    }


    public void delete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        //review.getReviewImage().deleteImage(filePath);
        reviewRepository.delete(review);
    }

    public ReviewResponse createReview(Member actor, Long productId, ReviewSaveRequest reviewSaveRequest) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow();

        CommonFile mainFile = fileTranslator.translateFile(reviewSaveRequest.getMainFile());
        List<CommonFile> subFiles = fileTranslator.translateFileList(reviewSaveRequest.getSubFiles());

        for(CommonFile subFile : subFiles){
            mainFile.connectFile(subFile);
        }

        Review review = Review.of(reviewSaveRequest, actor, product, mainFile);
        product.addReview(review.getRating()); //상품 리뷰수, 상품 평균 평점을 계산해서 저장
        reviewRepository.save(review);

        return ReviewResponse.of(review);
    }

    public ReviewResponse update(Long reviewId, ReviewSaveRequest reviewSaveRequest) throws IOException {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
//        ReviewImage reviewImage = review.getReviewImage();
//
//        //메인 파일 이름을 아예 있던거 없애고 바꿔버리기!
//        reviewImage.changeMainFile(filePath, createStoreFileName(mainFile.getOriginalFilename()), mainFile);
//
//        //서브 파일도 같은 방식으로
//        //비어있는 경우도 생각.,,?
//        //ReviewImage 엔티티에서 가능하니까 거기 메소드를 만들어보쟈
//        //reviewImage.changeSubFile(filePath, subFiles); => List<String>으로 만들어 보내야함
//        List<String> fileList = saveSubImages(filePath, subFiles);
//        reviewImage.changeSubFile(filePath, fileList);

        review.update(reviewSaveRequest);
        return ReviewResponse.of(review);
    }
}
