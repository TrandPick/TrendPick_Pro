package project.trendpick_pro.domain.review.service;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.filetranslator.FileTranslator;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewProductResponse;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.repository.ReviewRepository;
import project.trendpick_pro.global.rsData.RsData;

import java.io.IOException;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final FileTranslator fileTranslator;

    private final Rq rq;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("https://kr.object.ncloudstorage.com/{cloud.aws.s3.bucket}/")
    private String filePath;

    public ReviewResponse showReview(Long productId) {
        Review review = reviewRepository.findById(productId).orElseThrow();

        return ReviewResponse.of(review);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id).orElseThrow();
    }

    public Page<ReviewProductResponse> getProductReviews(Long productId, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), 6);
        return reviewRepository.findAllByProductId(productId, pageable);
    }

    @Transactional
    public RsData<ReviewResponse> createReview(Member actor, Long productId, ReviewSaveRequest reviewSaveRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow();

        CommonFile mainFile = fileTranslator.translateFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.translateFileList(requestSubFiles);

        for (CommonFile subFile : subFiles) {
            mainFile.connectFile(subFile);
        }

        Review review = Review.of(reviewSaveRequest, actor, product, mainFile);
        product.addReview(review.getRating()); //상품 리뷰수, 상품 평균 평점을 계산해서 저장
        reviewRepository.save(review);

        return RsData.of("S-1", "리뷰 등록이 완료되었습니다.", ReviewResponse.of(review));
    }

    @Transactional
    public RsData<ReviewResponse> modify(Long reviewId, ReviewSaveRequest reviewSaveRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException {
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        fileTranslator.deleteFile(review.getFile());
        review.disconnectFile();

        CommonFile mainFile = fileTranslator.translateFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.translateFileList(requestSubFiles);

        for (CommonFile subFile : subFiles) {
            mainFile.connectFile(subFile);
        }

        review.update(reviewSaveRequest, mainFile);

        return RsData.of("S-1", "리뷰 수정이 완료되었습니다.", ReviewResponse.of(review));
    }

    @Transactional
    public void delete(Long reviewId) {
        rq.getAdmin();
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        fileTranslator.deleteFile(review.getFile());
        reviewRepository.delete(review);
    }

    @Transactional
    public Page<ReviewResponse> showAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), 6);
        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        return reviewPage.map(ReviewResponse::of);
    }

    @Transactional
    public Page<ReviewResponse> showOwnReview(String writer, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), 6);
        Page<Review> reviewPage = reviewRepository.findByWriter(writer, pageable);
        return reviewPage.map(ReviewResponse::of);
    }
}
