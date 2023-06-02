package project.trendpick_pro.domain.recommend.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.brand.entity.QBrand;
import project.trendpick_pro.domain.common.file.QCommonFile;
import project.trendpick_pro.domain.member.entity.QMember;
import project.trendpick_pro.domain.product.entity.QProduct;
import project.trendpick_pro.domain.recommend.entity.QRecommend;
import project.trendpick_pro.domain.recommend.entity.Recommend;
import project.trendpick_pro.domain.recommend.entity.dto.QRecommendResponse;
import project.trendpick_pro.domain.recommend.entity.dto.RecommendResponse;

import java.util.List;

import static project.trendpick_pro.domain.brand.entity.QBrand.*;
import static project.trendpick_pro.domain.common.file.QCommonFile.*;
import static project.trendpick_pro.domain.member.entity.QMember.*;
import static project.trendpick_pro.domain.product.entity.QProduct.*;
import static project.trendpick_pro.domain.recommend.entity.QRecommend.*;

public class RecommendRepositoryImpl implements RecommendRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RecommendRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<RecommendResponse> findAllByMemberName(String username, Pageable pageable) {

        List<RecommendResponse> result = queryFactory
                .select(new QRecommendResponse(
                        product.id,
                        product.name,
                        brand.name,
                        commonFile.translatedFileName,
                        product.price
                ))
                .from(recommend)
                .leftJoin(recommend.member, member)
                .leftJoin(recommend.product, product)
                .leftJoin(recommend.brand, brand)
                .leftJoin(recommend.product.file, commonFile)
                .where(recommend.member.username.eq(username))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(recommend.count())
                .from(recommend)
                .leftJoin(recommend.member, member)
                .leftJoin(recommend.product, product)
                .leftJoin(recommend.brand, brand)
                .leftJoin(recommend.product.file, commonFile)
                .where(recommend.member.username.eq(username));

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }
}
