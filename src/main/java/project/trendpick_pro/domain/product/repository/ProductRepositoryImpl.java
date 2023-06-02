package project.trendpick_pro.domain.product.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.QMember;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.QProduct;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.dto.response.QProductListResponse;
import project.trendpick_pro.domain.product.entity.dto.response.QRecommendProductExResponse;
import project.trendpick_pro.domain.product.entity.dto.response.RecommendProductExResponse;
import project.trendpick_pro.domain.tag.entity.QTag;

import java.util.List;

import static project.trendpick_pro.domain.brand.entity.QBrand.*;
import static project.trendpick_pro.domain.category.entity.QMainCategory.*;
import static project.trendpick_pro.domain.category.entity.QSubCategory.*;
import static project.trendpick_pro.domain.common.file.QCommonFile.commonFile;
import static project.trendpick_pro.domain.member.entity.QMember.*;
import static project.trendpick_pro.domain.product.entity.QProduct.*;
import static project.trendpick_pro.domain.tag.entity.QTag.*;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em, MemberRepository memberRepository) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductListResponse> findAllByCategoryId(ProductSearchCond cond, Pageable pageable) {
        List<ProductListResponse> result = queryFactory
                .select(new QProductListResponse(
                        product.id,
                        product.name,
                        brand.name,
                        commonFile.translatedFileName,
                        product.price)
                )
                .from(product)
                .leftJoin(product.mainCategory, mainCategory)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.brand, brand)
                .leftJoin(product.file, commonFile)
                .where(
                        mainCategoryEq(cond)
                                .and(subCategoryEq(cond))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSelector(cond.getSortCode())) //정렬추가
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.mainCategory, mainCategory)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.brand, brand)
                .leftJoin(product.file, commonFile)
                .where(
                        mainCategoryEq(cond),
                        subCategoryEq(cond)
                );

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    //        select distinct T.product_id from tag T
//        where T.member_id = 1;
//    @Override
//    public List<RecommendProductExResponse> findAllRecommendProductEx(Member member) {
//
//        List<RecommendProductExResponse> list = queryFactory
//                .select(new QRecommendProductExResponse(
//                                tag.product.id,
//                                tag.name
//                        )
//                )
//                .from(tag)
//                .where(tag.member.id.eq(member.getId()))
//                .fetch();
//
//        return list;
//    }

    @Override
    public List<Product> findByTag(String username) {

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetchOne();

        NumberExpression<Integer> sum = tag.score.sum();

        assert findMember != null;
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.tags, tag)
                .where(tag.in(findMember.getTags()))
                .groupBy(product)
                .orderBy(sum.desc())
                .fetch();
    }

//    select T_P.product_id
//    from Tag T_P
//    where T_P.name in(
//        select T_M.name
//        from Tag T_M
//        where T_M.member_id = 1
//    )
    @Override
    public List<RecommendProductExResponse> findAllRecommendProductEx(Member member) {
        QTag tagByMember = new QTag("tagByMember");
        QTag tagByProduct = new QTag("tagByProduct");

        List<RecommendProductExResponse> list = queryFactory
                .select(new QRecommendProductExResponse(
                                tagByProduct.product.id,
                                tagByProduct.name
                        )
                )
                .from(tagByProduct)
                .where(tagByProduct.name.in(
                        JPAExpressions.select(tagByMember.name)
                                .from(tagByMember)
                                .where(tagByMember.member.id.eq(member.getId()))
                )
                        .and(tagByProduct.product.id.isNotNull()))
                .distinct()
                .fetch();

        return list;
    }


    private static BooleanExpression mainCategoryEq(ProductSearchCond cond) {
        return mainCategory.name.eq(cond.getMainCategory());
    }

    private static BooleanExpression subCategoryEq(ProductSearchCond cond) {
        return subCategory.name.eq(cond.getSubCategory());
    }

    private static OrderSpecifier<?> orderSelector(Integer sortCode) {
        return switch (sortCode) {
            case 2 -> product.id.asc();
            case 3 -> product.rateAvg.desc(); //평점
            case 4 -> product.rateAvg.asc();
            case 5 -> product.reviewCount.desc(); //리뷰수
            default -> product.id.desc();
        };
    }
}
