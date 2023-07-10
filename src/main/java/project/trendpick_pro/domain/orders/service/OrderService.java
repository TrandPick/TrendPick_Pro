package project.trendpick_pro.domain.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.Payload;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;

import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.exceoption.OrderNotFoundException;
import project.trendpick_pro.domain.orders.repository.OrderItemRepository;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.type.TagType;
import project.trendpick_pro.global.rsData.RsData;


import java.util.List;
import java.time.LocalDateTime;


public interface OrderService {
    RsData<Order> cartToOrder(Member member, CartToOrderRequest request);

    RsData<Order> productToOrder(Member member, Long id, int quantity, String size, String color);

    void orderToOrder(@Payload String Id)throws JsonProcessingException ;

    void sendMessage(String message, Long orderId, String email)throws JsonProcessingException;

    void message(@Payload String json)throws JsonProcessingException;

    RsData cancel(Long orderId);

    void delete(Long id);

    RsData<OrderDetailResponse> showOrderItems(Member member, Long orderId);

    Page<OrderResponse> findAllByMember(Member member, int offset);

    Page<OrderResponse> findAllBySeller(Member member, int offset);

    int settlementOfSales(Member member);

    Order findById(Long id);

    Page<OrderResponse> findCanceledOrders(Member member, int offset);

    List<OrderItem> findAllByPayDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate);
}