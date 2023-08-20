package project.trendpick_pro.domain.orders.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.delivery.entity.DeliveryState;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.exception.OrderNotFoundException;
import project.trendpick_pro.domain.orders.repository.OrderItemRepository;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.TagType;
import project.trendpick_pro.global.kafka.KafkaProducerService;
import project.trendpick_pro.global.kafka.kafkasave.entity.OutboxMessage;
import project.trendpick_pro.global.kafka.kafkasave.service.OutboxMessageService;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    private final CartService cartService;
    private final ProductService productService;
    private final FavoriteTagService favoriteTagService;

    private final OutboxMessageService outboxMessageService;

    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public RsData cartToOrder(Member member, CartToOrderRequest request) {
        if(member.getAddress().trim().isEmpty()) {
            return RsData.of("F-1", "주소를 알 수 없어 주문이 불가능합니다.");
        }
        if(request.getSelectedItems().isEmpty()){
            return RsData.of("F-1","상품을 선택한 후 주문해주세요.");
        }
        List<CartItem> cartItems = cartService.getSelectedCartItems(member, request);
        if (!Objects.equals(cartItems.get(0).getCart().getMember().getId(), member.getId())) {
            return RsData.of("F-1", "현재 접속중인 사용자와 장바구니 사용자가 일치하지 않습니다.");
        }
        Order order = createOrder(member, cartItems);

        OutboxMessage message = createOutboxMessage(order);
        kafkaProducerService.sendMessage(message.getId());
        return RsData.of("S-1", "주문을 시작 합니다.");
    }

    @Transactional
    public RsData productToOrder(Member member, Long productId, int quantity, String size, String color) {
        try {
            Order saveOrder = orderRepository.save(
                    Order.createOrder(member, new Delivery(member.getAddress()),
                            List.of(OrderItem.of(productService.findById(productId), quantity, size, color))
                    )
            );
            OutboxMessage message = createOutboxMessage(saveOrder);
            kafkaProducerService.sendMessage(message.getId());
            return RsData.of("S-1", "주문을 시작합니다.");
        } catch (ProductNotFoundException e) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
    }

    @Transactional
    public void tryOrder(String id) throws JsonProcessingException {
        Order order = orderRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문 입니다."));
        String email = order.getMember().getEmail();
        try {
            decreaseStock(order);
            order.updateStatus(OrderStatus.ORDERED);
            sendMessage(order.getId(), "Success", email);
        } catch (ProductStockOutException e) {
            if (order.getOrderStatus() == OrderStatus.TEMP) {
                order.cancelTemp();
            }
            log.error("주문 실패 : {}", e.getMessage());
            sendMessage(order.getId(), "Fail", email);
        }
    }

    @Transactional
    public RsData cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
        if(order.getOrderStatus() == OrderStatus.CANCELED) {
            return RsData.of("F-1", "이미 취소된 주문입니다.");
        } else if (order.getDelivery().getState() == DeliveryState.COMPLETED) {
            return RsData.of("F-2", "이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        if (order.getDelivery().getState() == DeliveryState.DELIVERY_ING) {
            return RsData.of("F-3", "이미 배송을 시작하여 취소가 불가능합니다.");
        }
        order.cancel();
        return RsData.of("S-1", "환불 요청이 정상적으로 진행 되었습니다. 환불까지는 최소 2일에서 최대 14일까지 소요될 수 있습니다.");
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
        orderRepository.delete(order);
    }

    public RsData<OrderDetailResponse> findOrderItems(Member member, Long orderId) {
        try {
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문 입니다."));
            if (order.getOrderStatus() == OrderStatus.TEMP) {
                orderRepository.delete(order);
                return RsData.of("F-3", "존재하지 않는 주문입니다.");
            }
            if (!Objects.equals(order.getMember().getId(), member.getId())) {
                return RsData.of("F-2", "다른 사용자의 주문에는 접근할 수 없습니다.");
            }
            OrderDetailResponse orderResponse = OrderDetailResponse.of(order, orderRepository.findOrderItemsByOrderId(orderId));
            return RsData.of("S-1", orderResponse.getOrderItems().size()+" 개의 주문 상품입니다.", orderResponse);
        } catch (OrderNotFoundException e) {
            return RsData.of("F-1", "잘못된 주문번호입니다. 다시 확인해주세요.");
        }
    }

    public Page<OrderResponse> findAll(Member member, int offset) {
        Page<OrderResponse> findOrders;
        findOrders = settingOrderByMemberStatus(member, offset);
        List<OrderResponse> validOrders = checkingTempStatus(findOrders);
        return new PageImpl<>(validOrders, findOrders.getPageable(), validOrders.size());
    }

    public int settlementOfSales(Member member, LocalDate registeredDate) {
        return orderRepository.findAllByMonth(new OrderSearchCond(member.getBrand()), registeredDate);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
    }

    public Page<OrderResponse> findCanceledOrders(Member member, int offset) {
        OrderSearchCond cond = new OrderSearchCond(member.getId(), OrderStatus.CANCELED);
        return orderRepository.findAllByMember(cond, PageRequest.of(offset, 10));
    }

    public List<OrderItem> findAllByCreatedDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderItemRepository.findAllByCreatedDateBetween(fromDate, toDate);
    }

    private Order createOrder(Member member, List<CartItem> cartItems) {
        List<OrderItem> orderItems = createOrderItem(member, cartItems);
        Order order = Order.createOrder(member, new Delivery(member.getAddress()), orderItems);
        return orderRepository.save(order);
    }

    private List<OrderItem> createOrderItem(Member member, List<CartItem> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = checkingProductStock(cartItem);
            favoriteTagService.updateTag(member, product, TagType.ORDER);
            orderItems.add(OrderItem.of(product, cartItem));
        }
        return orderItems;
    }

    private Product checkingProductStock(CartItem cartItem) {
        Product product = productService.findById(cartItem.getProduct().getId());
        if (product.getProductOption().getStock() < cartItem.getQuantity()) {
            throw new ProductStockOutException("재고가 부족합니다.");
        }
        return product;
    }

    private OutboxMessage createOutboxMessage(Order order) {
        OutboxMessage message = new OutboxMessage("orders",
                String.valueOf(order.getId()), String.valueOf(order.getId()));
        outboxMessageService.save(message);
        return message;
    }

    private void sendMessage(Long orderId, String message, String email) throws JsonProcessingException {
        kafkaProducerService.sendMessage(orderId, message, email);
    }

    private static void decreaseStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.getProduct().getProductOption().decreaseStock(orderItem.getQuantity());
            if (orderItem.getProduct().getProductOption().getStock() < 0) {
                throw new ProductStockOutException("재고가 부족 합니다.");
            }
            log.info("재고 : {}", orderItem.getProduct().getProductOption().getStock());
        }
    }

    private Page<OrderResponse> settingOrderByMemberStatus(Member member, int offset) {
        Page<OrderResponse> findOrders;
        PageRequest pageable = PageRequest.of(offset, 10);
        if (member.getRole().equals(MemberRoleType.MEMBER)) {
            OrderSearchCond cond = new OrderSearchCond(member.getId());
            findOrders = orderRepository.findAllByMember(cond, pageable);
        } else {
            OrderSearchCond cond = new OrderSearchCond(member.getBrand());
            findOrders = orderRepository.findAllBySeller(cond, pageable);
        }
        return findOrders;
    }

    private static List<OrderResponse> checkingTempStatus(Page<OrderResponse> findOrders) {
        return findOrders.stream()
                .filter(orderResponse -> !Objects.equals(orderResponse.getOrderStatus(), OrderStatus.TEMP.toString()))
                .collect(Collectors.toList());
    }
}