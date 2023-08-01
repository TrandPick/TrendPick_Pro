package project.trendpick_pro.domain.orders.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.QOrderResponse;
import project.trendpick_pro.domain.product.entity.product.QProduct;

import java.util.List;
import java.time.LocalDate;
import java.time.Month;

import static project.trendpick_pro.domain.member.entity.QMember.member;
import static project.trendpick_pro.domain.orders.entity.QOrder.order;
import static project.trendpick_pro.domain.orders.entity.QOrderItem.orderItem;
import static project.trendpick_pro.domain.product.entity.product.QProduct.*;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //나의 주문목록
    @Override
    public Page<OrderResponse> findAllByMember(OrderSearchCond orderSearchCond, Pageable pageable) {
        List<OrderResponse> result = queryFactory
                .select(new QOrderResponse(
                        order.id,
                        product.id,
                        product.productOption.file.fileName,
                        product.productOption.brand.name,
                        product.title,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        orderItem.discountPrice,
                        order.createdDate,
                        order.modifiedDate,
                        orderItem.size,
                        orderItem.color,
                        order.status.stringValue(),
                        order.delivery.state.stringValue())
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(orderItem.product, product)
                .join(order.member, member)
                .where(
                        statusEq(orderSearchCond),
                        memberIdEq(orderSearchCond)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderItem.order.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .join(order.member, member)
                .where(
                        memberIdEq(orderSearchCond)
                );
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    private static BooleanExpression memberIdEq(OrderSearchCond orderSearchCond) {
        return member.id.eq(orderSearchCond.getMemberId());
    }

    @Override
    public List<OrderResponse> findOrderItemsByOrderId(Long orderId) {
        return queryFactory
                .select(new QOrderResponse(
                        order.id,
                        product.id,
                        product.productOption.file.fileName,
                        product.productOption.brand.name,
                        product.title,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        orderItem.discountPrice,
                        order.createdDate,
                        order.modifiedDate,
                        orderItem.size,
                        orderItem.color,
                        order.status.stringValue(),
                        order.delivery.state.stringValue())
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(orderItem.product, product)
                .where(order.id.eq(orderId))
                .fetch();
    }

    @Override
    public Page<OrderResponse> findAllBySeller(OrderSearchCond orderSearchCond, Pageable pageable) {
        List<OrderResponse> result = queryFactory
                .select(new QOrderResponse(
                        order.id,
                        product.id,
                        product.productOption.file.fileName,
                        product.productOption.brand.name,
                        product.title,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        orderItem.discountPrice,
                        order.createdDate,
                        order.modifiedDate,
                        orderItem.size,
                        orderItem.color,
                        order.status.stringValue(),
                        order.delivery.state.stringValue())
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .leftJoin(orderItem.product, product)
                .join(order.member, member)
                .where(
                        brandEq(orderSearchCond)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderItem.order.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .join(order.member, member)
                .where(brandEq(orderSearchCond));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public int findAllByMonth(OrderSearchCond orderSearchCond) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();

        Integer result = queryFactory
                .select(orderItem.orderPrice.sum())
                .from(orderItem)
                .join(orderItem.order, order)
                .join(order.member, member)
                .where(brandEq(orderSearchCond),
                        Expressions.dateTemplate(
                                Integer.class,
                                "MONTH({0})",
                                orderItem.order.createdDate
                        ).eq(currentMonth),
                        orderItem.order.status.stringValue().eq("ORDERED"))
                .fetchOne();

        return result == null ? 0 : result;
    }

    private static BooleanExpression brandEq(OrderSearchCond orderSearchCond) {
        return product.productOption.brand.name.eq(orderSearchCond.getBrand());
    }

    private static BooleanExpression statusEq(OrderSearchCond orderSearchCond) {
        if(orderSearchCond.getStatus() == null)
            return null;
        return order.status.stringValue().eq(orderSearchCond.getStatus());
    }
}
