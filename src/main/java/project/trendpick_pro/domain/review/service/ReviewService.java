package project.trendpick_pro.domain.review.service;

import com.querydsl.core.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.filetranslator.FileTranslator;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.repository.ReviewRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final FileTranslator fileTranslator;

    @Value("${file.path}")
    private static String filePath;


    public ReviewResponse showReview(Long productId) {
        Review review = reviewRepository.findById(productId).orElseThrow();

        return ReviewResponse.of(review);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id).orElseThrow();
    }


    @Transactional
    public void delete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        reviewRepository.delete(review);
    }

    public ReviewResponse createReview(Member actor, Long productId, ReviewSaveRequest reviewSaveRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow();

        CommonFile mainFile = fileTranslator.translateFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.translateFileList(requestSubFiles);

        for(CommonFile subFile : subFiles){
            mainFile.connectFile(subFile);
        }

        Review review = Review.of(reviewSaveRequest, actor, product, mainFile);
        product.addReview(review.getRating()); //상품 리뷰수, 상품 평균 평점을 계산해서 저장
        reviewRepository.save(review);

        return ReviewResponse.of(review);
    }

    public ReviewResponse modify(Long reviewId, ReviewSaveRequest reviewSaveRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException {
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        CommonFile mainFile = review.getFile();
        List<CommonFile> subFiles = review.getFile().getChild();

        if(requestMainFile != null){
            //  기존 이미지 삭제
            FileUtils.delete(new File(mainFile.getFileName()));
        }
        // 이미지 업데이트
        mainFile = fileTranslator.translateFile(requestMainFile);

        if(requestSubFiles != null){
            // 기존 이미지 삭제
            for(CommonFile subFile : subFiles){
                FileUtils.delete(new File(subFile.getFileName()));
            }
        }
        // 이미지 업데이트
        subFiles = fileTranslator.translateFileList(requestSubFiles);

        for (CommonFile subFile : subFiles) {
            mainFile.connectFile(subFile);
        }

        review.update(reviewSaveRequest, mainFile);

        return ReviewResponse.of(review);
    }

    @Transactional
    public Page<ReviewResponse> showAll(Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        return reviewPage.map(ReviewResponse::of);
    }

//    public Page<ReviewResponse> showAll(int offset) {
//        Pageable pageable = PageRequest.of(offset, 20);
//        Page<Review> reviews = reviewRepository.findAllByPage(pageable);
//        return reviews.map(ReviewResponse::of);
//    }
}
