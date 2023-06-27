package project.trendpick_pro.domain.product.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.product.dto.response.*;
import project.trendpick_pro.domain.product.entity.productOption.QProductOption;

import java.util.List;

import static project.trendpick_pro.domain.brand.entity.QBrand.brand;
import static project.trendpick_pro.domain.category.entity.QMainCategory.*;
import static project.trendpick_pro.domain.category.entity.QSubCategory.*;
import static project.trendpick_pro.domain.common.file.QCommonFile.commonFile;
import static project.trendpick_pro.domain.product.entity.product.QProduct.*;
import static project.trendpick_pro.domain.product.entity.productOption.QProductOption.*;
import static project.trendpick_pro.domain.tags.favoritetag.entity.QFavoriteTag.favoriteTag;
import static project.trendpick_pro.domain.tags.tag.entity.QTag.tag;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductListResponse> findAllByCategoryId(ProductSearchCond cond, Pageable pageable) {
        List<ProductListResponse> result = queryFactory
                .select(new QProductListResponse(
                        product.id,
                        product.name,
                        brand.name,
                        commonFile.fileName,
                        productOption.price,
                        product.discountRate,
                        product.discountedPrice
                        )
                )
                .from(product)
                .leftJoin(product.mainCategory, mainCategory)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.brand, brand)
                .leftJoin(product.productOption, productOption)
                .leftJoin(product.file, commonFile)
                .where(
                        mainCategoryEq(cond)
                                .and(subCategoryEq(cond))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
//                .orderBy(orderSelector(cond.getSortCode())) //정렬추가
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.mainCategory, mainCategory)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.brand, brand)
                .leftJoin(product.productOption, productOption)
                .leftJoin(product.file, commonFile)
                .where(
                        mainCategoryEq(cond),
                        subCategoryEq(cond)
                );

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    @Override
    public List<ProductByRecommended> findRecommendProduct(String username) {
        List<ProductByRecommended> list = queryFactory
                .select(new QProductByRecommended(
                                tag.product.id,
                                tag.name
                        )
                )
                .from(tag)
                .where(tag.name.in(
                                JPAExpressions.select(favoriteTag.name)
                                        .from(favoriteTag)
                                        .where(favoriteTag.member.username.eq(username))
                        )
                )
                .distinct()
                .fetch();

        return list;
    }

    @Override
    public Page<ProductListResponseBySeller> findAllBySeller(String brand, Pageable pageable) {
        List<ProductListResponseBySeller> list = queryFactory
                .select(new QProductListResponseBySeller(
                        product.id,
                        product.name,
                        commonFile.fileName,
                        productOption.price,
                        productOption.stock,
                        product.createdDate,
                        product.saleCount,
                        product.rateAvg,
                        product.reviewCount,
                        product.askCount,
                        product.discountRate,
                        product.discountedPrice
                ))
                .from(product)
                .leftJoin(product.file, commonFile)
                .leftJoin(product.productOption, productOption)
                .where(product.brand.name.eq(brand))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdDate.desc()) //정렬추가
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.file, commonFile)
                .where(product.brand.name.eq(brand));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    @Override
    public Page<ProductListResponse> findAllByKeyword(ProductSearchCond cond, Pageable pageable) {
        List<ProductListResponse> result = queryFactory
                .select(new QProductListResponse(
                        product.id,
                        product.name,
                        brand.name,
                        commonFile.fileName,
                        productOption.price,
                        product.discountRate,
                        product.discountedPrice
                        )
                )
                .from(product)
                .leftJoin(product.mainCategory, mainCategory)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.brand, brand)
                .leftJoin(product.productOption, productOption)
                .leftJoin(product.file, commonFile)
                .where(
                        product.name.contains(cond.getKeyword())
                    .or(brand.name.contains(cond.getKeyword()))
                    .or(mainCategory.name.contains(cond.getKeyword()))
                    .or(subCategory.name.contains(cond.getKeyword()))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.mainCategory, mainCategory)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.brand, brand)
                .leftJoin(product.file, commonFile)
                .where(
                        product.name.contains(cond.getKeyword())
                                .or(brand.name.contains(cond.getKeyword()))
                                .or(mainCategory.name.contains(cond.getKeyword()))
                                .or(subCategory.name.contains(cond.getKeyword()))
                );

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
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

    private static OrderSpecifier<?>
    orderSelector(Integer sortCode) {
        return switch (sortCode) {
            case 2 -> product.id.asc();
            case 3 -> product.rateAvg.desc(); //평점
            case 4 -> product.rateAvg.asc();
            case 5 -> product.reviewCount.desc(); //리뷰수
            default -> product.id.desc();
        };
    }
}