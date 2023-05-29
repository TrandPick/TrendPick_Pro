package project.trendpick_pro.domain.ask.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.ask.entity.QAsk;
import project.trendpick_pro.domain.ask.entity.dto.request.AskByProductRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskByProductResponse;
import project.trendpick_pro.domain.ask.entity.dto.response.QAskByProductResponse;

import java.util.List;

import static project.trendpick_pro.domain.ask.entity.QAsk.ask;
import static project.trendpick_pro.domain.product.entity.QProduct.product;

public class AskRepositoryImpl implements AskRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AskRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AskByProductResponse> findAllByProduct(AskByProductRequest request, Pageable pageable) {
        List<AskByProductResponse> result = queryFactory
                .select(new QAskByProductResponse(
                        ask.id,
                        ask.state,
                        ask.title,
                        ask.author.username,
                        ask.createdDate
                ))
                .from(ask)
                .leftJoin(ask.product, product)
                .where(
                        askByProductEq(request)
                )
                .offset(0)
                .limit(10)
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(ask.count())
                .from(ask)
                .leftJoin(ask.product, product)
                .where(
                        askByProductEq(request)
                );

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private static BooleanExpression askByProductEq(AskByProductRequest request) {
        return ask.product.id.eq(request.getProductId());
    }
}
