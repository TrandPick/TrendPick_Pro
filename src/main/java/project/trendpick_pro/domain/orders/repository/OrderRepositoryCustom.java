package project.trendpick_pro.domain.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;

public interface OrderRepositoryCustom {
    public Page<OrderResponse> findAllByMember(OrderSearchCond orderSearchCond, Pageable pageable);
}
