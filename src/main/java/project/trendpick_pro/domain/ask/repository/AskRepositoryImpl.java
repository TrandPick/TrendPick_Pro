package project.trendpick_pro.domain.ask.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.entity.dto.response.QAskResponse;

import java.util.List;

import static project.trendpick_pro.domain.ask.entity.QAsk.ask;
import static project.trendpick_pro.domain.member.entity.QMember.*;
import static project.trendpick_pro.domain.product.entity.QProduct.product;

public class AskRepositoryImpl implements AskRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AskRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AskResponse> findAllByProductId(Long productId, Pageable pageable) {
        List<AskResponse> result = queryFactory
                .select(new QAskResponse(
                        ask.id,
                        product.id,
                        member.username,
                        member.id,
                        ask.title,
                        ask.status.stringValue(),
                        ask.createdDate
                ))
                .from(ask)
                .join(ask.product, product)
                .on(askByProductIdEq(productId))
                .join(ask.author, member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(ask.count())
                .from(ask)
                .join(ask.product, product)
                .on(askByProductIdEq(productId))
                .join(ask.author, member);

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private static BooleanExpression askByProductIdEq(Long productId) {
        return product.id.eq(productId);
    }
}
