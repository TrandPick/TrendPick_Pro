//package project.trendpick_pro.domain.ask.repository;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQuery;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.support.PageableExecutionUtils;
//import project.trendpick_pro.domain.ask.entity.QAsk;
//import project.trendpick_pro.domain.ask.entity.dto.request.AskByProductRequest;
//import project.trendpick_pro.domain.ask.entity.dto.response.AskByProductResponse;
//import project.trendpick_pro.domain.ask.entity.dto.response.QAskResponse;
//import project.trendpick_pro.domain.member.entity.QMember;
//import project.trendpick_pro.domain.product.entity.QProduct;
//
//import java.util.List;
//
//import static com.querydsl.core.QueryModifiers.offset;
//import static project.trendpick_pro.domain.ask.entity.QAsk.*;
//import static project.trendpick_pro.domain.brand.entity.QBrand.brand;
//import static project.trendpick_pro.domain.category.entity.QMainCategory.mainCategory;
//import static project.trendpick_pro.domain.category.entity.QSubCategory.subCategory;
//import static project.trendpick_pro.domain.common.file.QCommonFile.commonFile;
//import static project.trendpick_pro.domain.member.entity.QMember.*;
//import static project.trendpick_pro.domain.product.entity.QProduct.product;
//
//public class AskRepositoryImpl implements AskRepositoryCustom {
//
//    private final JPAQueryFactory queryFactory;
//
//    public AskRepositoryImpl(EntityManager em) {
//        this.queryFactory = new JPAQueryFactory(em);
//    }
//
//    @Override
//    public Page<AskByProductResponse> findAllByProduct(AskByProductRequest request, Pageable pageable) {
//        List<AskByProductResponse> result = queryFactory
//                .select(new QAskByProductResponse(
//                        ask.id,
//                        ask.title,
//                        ask.author.username,
//                        ask.createdDate
//                ))
//                .from(ask)
//                .leftJoin(ask.product, product)
//                .where(
//                        askByProductEq(request)
//                )
//                .offset(0)
//                .limit(10)
//                .fetch();
//
//
//        return null;
//    }
//
//    private static BooleanExpression askByProductEq(AskByProductRequest request) {
//        return ask.product.id.eq(request.getProductId());
//    }
//}
