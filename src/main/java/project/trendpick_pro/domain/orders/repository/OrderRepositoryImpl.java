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

import java.util.List;
import java.time.LocalDate;
import java.time.Month;

import static project.trendpick_pro.domain.member.entity.QMember.member;
import static project.trendpick_pro.domain.orders.entity.QOrder.order;
import static project.trendpick_pro.domain.orders.entity.QOrderItem.orderItem;

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
                        orderItem.order.id,
                        orderItem.product.id,
                        orderItem.product.file.fileName,
                        orderItem.product.brand.name,
                        orderItem.product.name,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        orderItem.discountPrice,
                        orderItem.order.createdDate,
                        orderItem.order.modifiedDate,
                        orderItem.size,
                        orderItem.color,
                        orderItem.order.status.stringValue(),
                        orderItem.order.delivery.state.stringValue())
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .join(order.member, member)
                .on(member.id.eq(orderSearchCond.getMemberId()))
                .where(statusEq(orderSearchCond))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderItem.order.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .join(order.member, member)
                .on(member.id.eq(orderSearchCond.getMemberId()))
                ;
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public List<OrderResponse> findOrderItemsByOrderId(Long orderId) {
        return queryFactory
                .select(new QOrderResponse(
                        orderItem.order.id,
                        orderItem.product.id,
                        orderItem.product.file.fileName,
                        orderItem.product.brand.name,
                        orderItem.product.name,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        orderItem.discountPrice,
                        orderItem.order.createdDate,
                        orderItem.order.modifiedDate,
                        orderItem.size,
                        orderItem.color,
                        orderItem.order.status.stringValue(),
                        orderItem.order.delivery.state.stringValue())
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .where(order.id.eq(orderId))
                .fetch();
    }

    //나의 판매목록
    @Override
    public Page<OrderResponse> findAllBySeller(OrderSearchCond orderSearchCond, Pageable pageable) {
        List<OrderResponse> result = queryFactory
                .select(new QOrderResponse(
                        orderItem.order.id,
                        orderItem.product.id,
                        orderItem.product.file.fileName,
                        orderItem.product.brand.name,
                        orderItem.product.name,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        orderItem.discountPrice,
                        orderItem.order.createdDate,
                        orderItem.order.modifiedDate,
                        orderItem.size,
                        orderItem.color,
                        orderItem.order.status.stringValue(),
                        orderItem.order.delivery.state.stringValue())
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .join(order.member, member)
                .on(orderItem.product.brand.name.eq(orderSearchCond.getBrand()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderItem.order.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .join(order.member, member)
                .on(orderItem.product.brand.name.eq(orderSearchCond.getBrand()));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public List<OrderResponse> findAllByMonth(OrderSearchCond orderSearchCond) {
        // 현재 날짜를 가져옴
        LocalDate currentDate = LocalDate.now();

        // 현재 월을 가져옴
        int currentMonth = currentDate.getMonthValue();

        List<OrderResponse> result = queryFactory
                .select(new QOrderResponse(
                        orderItem.order.id,
                        orderItem.product.id,
                        orderItem.product.file.fileName,
                        orderItem.product.brand.name,
                        orderItem.product.name,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        orderItem.discountPrice,
                        orderItem.order.createdDate,
                        orderItem.order.modifiedDate,
                        orderItem.size,
                        orderItem.color,
                        orderItem.order.status.stringValue(),
                        orderItem.order.delivery.state.stringValue())
                )
                .from(orderItem)
                .join(orderItem.order, order)
                .join(order.member, member)
                .on(orderItem.product.brand.name.eq(orderSearchCond.getBrand()))
                .where(Expressions.dateTemplate(
                        Integer.class,
                        "MONTH({0})",
                        orderItem.order.createdDate
                ).eq(currentMonth))
                .orderBy(orderItem.order.createdDate.desc())
                .fetch();

        return result;
    }


    //동적 처리, 환불 내역을 볼 때만 값을 지정
    private static BooleanExpression statusEq(OrderSearchCond orderSearchCond) {
        if(orderSearchCond.getStatus() == null)
            return null;
        return order.status.stringValue().eq(orderSearchCond.getStatus());
    }
}
