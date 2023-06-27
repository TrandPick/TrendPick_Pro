package project.trendpick_pro.domain.recommend.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.product.dto.response.QProductListResponse;

import java.util.List;

import static project.trendpick_pro.domain.member.entity.QMember.member;
import static project.trendpick_pro.domain.product.entity.product.QProduct.product;
import static project.trendpick_pro.domain.product.entity.productOption.QProductOption.productOption;
import static project.trendpick_pro.domain.recommend.entity.QRecommend.recommend;

public class RecommendRepositoryImpl implements RecommendRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RecommendRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductListResponse> findAllByMemberName(String username, Pageable pageable) {

        List<ProductListResponse> result = queryFactory
                .select(new QProductListResponse(
                        product.id,
                        product.name,
                        product.brand.name,
                        product.file.fileName,
                        productOption.price,
                        product.discountRate,
                        product.discountedPrice
                ))
                .from(recommend)
                .leftJoin(recommend.member, member)
                .leftJoin(recommend.product, product)
                .leftJoin(product.productOption, productOption)
                .where(recommend.member.username.eq(username))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(recommend.count())
                .from(recommend)
                .leftJoin(recommend.member, member)
                .leftJoin(recommend.product, product)
                .where(recommend.member.username.eq(username));

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }
}
