package project.trendpick_pro.domain.review.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.ReviewImage;
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

    @Value("${file.dir}")
    private static String filePath;


    public ReviewResponse showReview(Long productId) {
        Review review = reviewRepository.findById(productId).orElseThrow();

        return ReviewResponse.of(review);
    }


    public void delete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        review.getReviewImage().deleteImage(filePath);
        reviewRepository.delete(review);
    }

    public ReviewResponse createReview(Member actor, Long productId, ReviewCreateRequest reviewCreateRequest, MultipartFile mainFile, List<MultipartFile> subFiles) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow();
        //사진 없는 리뷰만 일단 저장
        Review review = new Review(actor, product, reviewCreateRequest);
        reviewRepository.save(review);

        //메인 이미지 파일 저장
        String mainFileName = saveMainFile(mainFile);
        ReviewImage reviewImage = new ReviewImage(mainFileName, review);
        reviewImageRepository.save(reviewImage);

        //서브 이미지 파일 저장
        saveSubFiles(subFiles, reviewImage);
        review.matchReviewImage(reviewImage);

        return ReviewResponse.of(review);
    }

    public ReviewResponse update(Long reviewId, ReviewUpdateRequest reviewUpdateRequest, MultipartFile mainFile, List<MultipartFile> subFiles) throws IOException {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        ReviewImage reviewImage = review.getReviewImage();

        //메인 파일 이름을 아예 있던거 없애고 바꿔버리기!
        reviewImage.changeMainFile(filePath, createStoreFileName(mainFile.getOriginalFilename()), mainFile);

        //서브 파일도 같은 방식으로
        //비어있는 경우도 생각.,,?
        //ReviewImage 엔티티에서 가능하니까 거기 메소드를 만들어보쟈
        //reviewImage.changeSubFile(filePath, subFiles); => List<String>으로 만들어 보내야함
        List<String> fileList = saveSubImages(filePath, subFiles);
        reviewImage.changeSubFile(filePath, fileList);

        review.update(reviewUpdateRequest);
        return ReviewResponse.of(review);
    }

    public static String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    public static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public static List<String> saveSubImages(String filePath, List<MultipartFile> subFiles) throws IOException {

        List<String> subFilePaths = new ArrayList<>();

        for (MultipartFile subFile : subFiles) {
            if (subFile.getOriginalFilename() != null) {
                String savePath = createStoreFileName(subFile.getOriginalFilename());
                subFilePaths.add(savePath);
                subFile.transferTo(new File(filePath + savePath));
            }
        }
        return subFilePaths;
    }

    private String saveMainFile(MultipartFile mainFile) throws IOException{
        String mainFileName = createStoreFileName(mainFile.getOriginalFilename());
        mainFile.transferTo(new File(filePath + mainFileName));

        return mainFileName;
    }

    private void saveSubFiles(List<MultipartFile> subFiles, ReviewImage reviewImage) throws IOException{
        List<String> subFilePaths = saveSubImages(filePath, subFiles);
        reviewImage.saveSubFileNames(subFilePaths);
    }

}
