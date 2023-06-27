package project.trendpick_pro.domain.review.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.review.entity.dto.response.QReviewProductResponse;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewProductResponse;

import java.util.List;

import static project.trendpick_pro.domain.common.file.QCommonFile.commonFile;
import static project.trendpick_pro.domain.review.entity.QReview.review;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<ReviewProductResponse> findAllByProductId(Long productId, Pageable pageable) {
        List<ReviewProductResponse> result = queryFactory
                .select(new QReviewProductResponse(
                        review.id,
                        review.writer,
                        review.title,
                        review.content,
                        commonFile.fileName,
                        review.rating))
                .from(review)
                .join(review.file, commonFile)
                .where(review.product.id.eq(productId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> CountQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.product.id.eq(productId));

        return PageableExecutionUtils.getPage(result, pageable, CountQuery::fetchOne);
    }

//    @Override
//    public Page<ReviewProductResponse> findAllByProductId(Long productId, Pageable pageable) {
//        List<ReviewProductResponse> reviewProductResponses = queryFactory
//                .select(Projections.constructor(ReviewProductResponse.class,
//                        qReview.id,
//                        qReview.writer,
//                        qReview.title,
//                        qReview.content,
//                        qReview.rating))
//                .from(qReview)
//                .where(qReview.product.id.eq(productId))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long totalCount = queryFactory
//                .selectFrom(qReview)
//                .where(qReview.product.id.eq(productId))
//                .fetchCount();
//
//        return new PageImpl<>(reviewProductResponses, pageable, totalCount);
//    }
}
//        long totalCount = queryFactory.selectFrom(qReview)
//                .where(qReview.product.id.eq(productId))
//                .fetchCount();