package project.trendpick_pro.domain.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;

import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.global.util.rsData.RsData;


import java.util.List;
import java.time.LocalDateTime;

public interface OrderService {
    RsData<Order> cartToOrder(Member member, CartToOrderRequest request);
    RsData<Order> productToOrder(Member member, Long id, int quantity, String size, String color);
    void tryOrder(String id)  throws JsonProcessingException;
    RsData cancel(Long orderId);
    void delete(Long id);
    RsData<OrderDetailResponse> showOrderItems(Member member, Long orderId);
    Page<OrderResponse> findAll(Member member, int offset);
    int settlementOfSales(Member member);
    Order findById(Long id);
    Page<OrderResponse> findCanceledOrders(Member member, int offset);
    List<OrderItem> findAllByPayDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate);
}