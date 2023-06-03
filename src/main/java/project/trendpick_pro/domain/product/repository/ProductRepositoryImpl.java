package project.trendpick_pro.domain.product.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.dto.response.QProductListResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static project.trendpick_pro.domain.brand.entity.QBrand.*;
import static project.trendpick_pro.domain.category.entity.QMainCategory.*;
import static project.trendpick_pro.domain.category.entity.QSubCategory.*;
import static project.trendpick_pro.domain.common.file.QCommonFile.commonFile;
import static project.trendpick_pro.domain.favoritetag.entity.QFavoriteTag.favoriteTag;
import static project.trendpick_pro.domain.member.entity.QMember.*;
import static project.trendpick_pro.domain.product.entity.QProduct.*;
import static project.trendpick_pro.domain.tag.entity.QTag.*;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final JPAQuery<Long> subQuery;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.subQuery = new JPAQuery<>(em);
    }

    @Override
    public Page<ProductListResponse> findAllByCategoryId(ProductSearchCond cond, Pageable pageable) {
        List<ProductListResponse> result = queryFactory
                .select(new QProductListResponse(
                        product.id,
                        product.name,
                        brand.name,
                        commonFile.fileName,
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

    @Override
    public List<Product> findProductByRecommended(String username) {

        List<Tuple> sortProducts = queryFactory
                .select(product.id, favoriteTag.score.sum())
                .from(product)
                .leftJoin(favoriteTag.member, member)
                .where(favoriteTag.name.in(
                        JPAExpressions.select(tag.name)
                        .from(tag)
                        .where(tag.product.eq(product)
                        )),
                        member.username.eq(username))
                .groupBy(product.id)
                .orderBy(favoriteTag.score.sum().desc())
                .fetch();

        return sortProducts.stream()
                .sorted(Comparator.comparing(
                        tuple -> Optional.ofNullable(tuple.get(1, Long.class)).orElse(Long.MIN_VALUE),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(tuple -> queryFactory
                        .selectFrom(product)
                        .where(product.id.eq(tuple.get(0, Long.class)))
                        .fetchOne())
                .toList();
    }

    private static BooleanExpression mainCategoryEq(ProductSearchCond cond) {
        return mainCategory.name.eq(cond.getMainCategory());
    }

    private static BooleanExpression subCategoryEq(ProductSearchCond cond) {
        if (cond.getSubCategory().equals("전체")) {
            return null;
        } else {
            return subCategory.name.eq(cond.getSubCategory());
        }
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
