package project.trendpick_pro.domain.review.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.common.file.QCommonFile;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.QOrderResponse;
import project.trendpick_pro.domain.orders.repository.OrderRepositoryCustom;
import project.trendpick_pro.domain.review.entity.QReview;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.response.QReviewResponse;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewProductResponse;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;

import java.util.List;
import java.util.stream.Collectors;

import static project.trendpick_pro.domain.member.entity.QMember.member;
import static project.trendpick_pro.domain.orders.entity.QOrder.order;
import static project.trendpick_pro.domain.orders.entity.QOrderItem.orderItem;
import static project.trendpick_pro.domain.review.entity.QReview.review;

@Repository

public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReview qReview = QReview.review;
    private final QCommonFile qCommonFile = QCommonFile.commonFile;


    public ReviewRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    @Override
    public Page<ReviewProductResponse> findAllByProductId(Long productId, Pageable pageable) {
        List<ReviewProductResponse> reviewProductResponses = queryFactory
                .select(Projections.constructor(ReviewProductResponse.class,
                        qReview.id,
                        qReview.writer,
                        qReview.title,
                        qReview.content,
                        qReview.file.fileName,
                        qReview.rating))
                .from(qReview)
                .join(qReview.file)
                .where(qReview.product.id.eq(productId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(qReview)
                .where(qReview.product.id.eq(productId))
                .fetchCount();

        return new PageImpl<>(reviewProductResponses, pageable, totalCount);
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