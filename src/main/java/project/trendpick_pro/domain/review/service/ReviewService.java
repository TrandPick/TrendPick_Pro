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



public interface ReviewService {
    ReviewResponse showReview(Long productId);
    Review findById(Long id);
    Page<ReviewProductResponse> getProductReviews(Long productId, Pageable pageable);
    RsData<ReviewResponse> createReview(Member actor, Long productId, ReviewSaveRequest reviewSaveRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws Exception;
    RsData<ReviewResponse> modify(Long reviewId, ReviewSaveRequest reviewSaveRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException;
    void delete(Long reviewId);
    Page<ReviewResponse> showAll(Pageable pageable);
    Page<ReviewResponse> showOwnReview(String writer, Pageable pageable);
}
