package project.trendpick_pro.domain.product.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.dto.response.QProductListResponse;

import java.util.List;

import static project.trendpick_pro.domain.brand.entity.QBrand.*;
import static project.trendpick_pro.domain.category.entity.QMainCategory.*;
import static project.trendpick_pro.domain.category.entity.QSubCategory.*;
import static project.trendpick_pro.domain.common.file.QCommonFile.commonFile;
import static project.trendpick_pro.domain.product.entity.QProduct.product;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductListResponse> findAllByCategoryId(ProductSearchCond cond, Pageable pageable) {
        List<ProductListResponse> result = queryFactory
//                .select(Projections.constructor(ProductListResponse.class,
//                        product.id,
//                        product.name,
//                        brand.name,
//                        product.file.translatedFileName,
//                        product.price))
                .select(new QProductListResponse(
                        product.id,
                        product.name,
                        brand.name,
                        commonFile.translatedFileName,
                        product.price
                ))
                .from(product)
                .leftJoin(product.mainCategory, mainCategory)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.brand, brand)
                .leftJoin(product.file, commonFile)
                .where(
                        mainCategoryEq(cond)
                                .and(subCategoryEq(cond))
                )
                .offset(0)
                .limit(18)
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

    private static BooleanExpression mainCategoryEq(ProductSearchCond cond) {
        return mainCategory.name.eq(cond.getMainCategory());
    }

    private static BooleanExpression subCategoryEq(ProductSearchCond cond) {
        return subCategory.name.eq(cond.getSubCategory());
    }

    private static OrderSpecifier<?> orderSelector(Integer sortCode) {
        return switch (sortCode) {
            case 2 -> product.id.asc();
//            case 3 -> product.getRateAvg.desc(); //평점
//            case 4 -> product.getRateAvg.asc();;
//            case 5 -> product.totalSales.desc();
            default -> product.id.desc();
        };
    }
}
