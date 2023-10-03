package project.trendpick_pro.domain.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.global.kafka.outbox.entity.OrderMaterial;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    RsData cartToOrder(Member member, CartToOrderRequest request);
    RsData<Long> productToOrder(Member member, Long id, int quantity, String size, String color);
    void tryOrder(String id, List<OrderMaterial> orderMaterials) throws JsonProcessingException;
    RsData cancel(Long orderId);
    void delete(Long id);
    RsData<OrderDetailResponse> findOrderItems(Member member, Long orderId);
    Page<OrderResponse> findAll(Member member, int offset);
    int settlementOfSales(Member member, LocalDate registeredDate);
    Order findById(Long id);
    Page<OrderResponse> findCanceledOrders(Member member, int offset);
    List<OrderItem> findAllByCreatedDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate);
    RsData<Order> getOrderFormData(Long orderId);
}