package project.trendpick_pro.domain.review.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.filetranslator.FileTranslator;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewCreateRequest;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewUpdateRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.repository.ReviewImageRepository;
import project.trendpick_pro.domain.review.repository.ReviewRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    public ReviewResponse createReview(Member actor, Long productId, ReviewCreateRequest reviewCreateRequest) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow();

        CommonFile mainFile = fileTranslator.translateFile(reviewCreateRequest.getMainFile());
        List<CommonFile> subFiles = fileTranslator.translateFileList(reviewCreateRequest.getSubFiles());

        for(CommonFile subFile : subFiles){
            mainFile.connectFile(subFile);
        }

        Review review = Review.of(reviewCreateRequest, actor, product, mainFile);
        product.addReview(review.getRating()); //상품 리뷰수, 상품 평균 평점을 계산해서 저장
        reviewRepository.save(review);

        return ReviewResponse.of(review);
    }

    public ReviewResponse update(Long reviewId, ReviewUpdateRequest reviewUpdateRequest) throws IOException {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        review.getFile();
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

        review.update(reviewUpdateRequest);
        return ReviewResponse.of(review);
    }
}
