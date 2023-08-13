package project.trendpick_pro.domain.orders.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.QOrderResponse;

import java.time.LocalDate;
import java.util.List;

import static project.trendpick_pro.domain.member.entity.QMember.member;
import static project.trendpick_pro.domain.orders.entity.QOrder.order;
import static project.trendpick_pro.domain.orders.entity.QOrderItem.orderItem;
import static project.trendpick_pro.domain.product.entity.product.QProduct.product;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

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
                        order.orderStatus.stringValue(),
                        order.delivery.state.stringValue())
                )
                .from(orderItem)
                .innerJoin(orderItem.order, order)
                .innerJoin(orderItem.product, product)
                .innerJoin(order.member, member)
                .where(
                        statusEq(orderSearchCond),
                        memberIdEq(orderSearchCond)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .innerJoin(order.member, member)
                .where(
                        memberIdEq(orderSearchCond)
                );

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
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
                        order.orderStatus.stringValue(),
                        order.delivery.state.stringValue())
                )
                .from(orderItem)
                .innerJoin(orderItem.order, order)
                .innerJoin(orderItem.product, product)
                .where(
                        orderIdEq(orderId)
                )
                .fetch();
    }

    @Override
    public Page<OrderResponse> findAllBySeller(OrderSearchCond cond, Pageable pageable) {
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
                        order.orderStatus.stringValue(),
                        order.delivery.state.stringValue())
                )
                .from(orderItem)
                .innerJoin(orderItem.order, order)
                .innerJoin(orderItem.product, product)
                .innerJoin(order.member, member)
                .where(
                        brandIdEq(cond)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderItem.order.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .innerJoin(orderItem.order, order)
                .innerJoin(orderItem.product, product)
                .innerJoin(order.member, member)
                .where(
                        brandIdEq(cond)
                );

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public int findAllByMonth(OrderSearchCond cond, LocalDate registeredDate) {

        int currentMonth = registeredDate.getMonthValue();

        Integer result = queryFactory
                .select(orderItem.orderPrice.sum())
                .from(orderItem)
                .join(orderItem.order, order)
                .join(order.member, member)
                .where(brandIdEq(cond),
                        Expressions.dateTemplate(
                                Integer.class,
                                "MONTH({0})",
                                orderItem.order.createdDate
                        ).eq(currentMonth),
                        orderItem.order.orderStatus.stringValue().eq("ORDERED"))
                .fetchOne();

        return result == null ? 0 : result;
    }

    private static BooleanExpression statusEq(OrderSearchCond cond) {
        return cond.getStatus() == null ? null : order.orderStatus.stringValue().eq(cond.getStatus());
    }

    private static BooleanExpression memberIdEq(OrderSearchCond cond) {
        return cond.getMemberId() == null ? null : member.id.eq(cond.getMemberId());
    }

    private static BooleanExpression brandIdEq(OrderSearchCond cond) {
        return cond.getBrand() == null ? null : product.productOption.brand.name.eq(cond.getBrand());
    }

    private static BooleanExpression orderIdEq(Long orderId) {
        return orderId == null ? null : order.id.eq(orderId);
    }
}
