package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepositoryCustom {
    public Page<OrderResponse> findAllByMember(OrderSearchCond orderSearchCond, Pageable pageable);
    public List<OrderResponse> findOrderItemsByOrderId(Long orderId);
    Page<OrderResponse> findAllBySeller(OrderSearchCond orderSearchCond, Pageable pageable);
    int findAllByMonth(OrderSearchCond orderSearchCond, LocalDate registeredDateTime);
}
