package project.trendpick_pro.domain.orders.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.global.kafka.KafkaProducerService;
import project.trendpick_pro.global.kafka.outbox.entity.OrderMaterial;
import project.trendpick_pro.global.kafka.outbox.service.OutboxMessageService;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
    private final OutboxMessageService outboxMessageService;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public RsData cartToOrder(Member member, CartToOrderRequest request) {
        List<CartItem> cartItems = cartService.getSelectedCartItems(member, request);
        RsData result = validateCartToOrder(member, request, cartItems);
        if (result.isFail()) return result;

        String uuidCode =  UUID.randomUUID().toString();
        List<OrderMaterial> orderMaterials = new ArrayList<>();

        List<OrderItem> orderItems = createOrderItems(cartItems);
        Order order = orderRepository.save(Order.createOrder(member, new Delivery(member.getAddress()), orderItems));

        return RsData.of("S-1", "주문을 시작 합니다.", order.getId());
    }


    @Transactional
    public RsData<Long> productToOrder(Member member, Long productId, int quantity, String size, String color) {
        String uuidCode =  UUID.randomUUID().toString();

        List<OrderItem> orderItems = createOrderItems(productId, quantity, size, color);
        Order order = orderRepository.save(
                Order.createOrder(member, new Delivery(member.getAddress()), orderItems));

        return RsData.of("S-1", "주문을 시작합니다.", order.getId());
    }

    @Transactional //실제 주문 로직 (재고 유효성 검사, 재고 감소, 상태 변경)
    public void tryOrder(String id, List<OrderMaterial> orderMaterials) throws JsonProcessingException {
        Order order = findById(Long.valueOf(id));
        if(order.getOrderState().equals("결제완료")) //멱등성

            throw new IllegalAccessError("이미 처리된 주문입니다.");

        try{
            List<OrderItem> orderItems = orderMaterialsToOrderItems(orderMaterials);
            order.settingOrderItems(orderItems);
            order.updateStatus(OrderStatus.TEMP);
            outboxMessageService.publishOrderProcessMessage("standByOrder", "Success", String.valueOf(order.getId()), order.getMember().getEmail());
        } catch(ProductStockOutException e){
            log.error("error message : {} ", e.getMessage());
            kafkaProducerService.sendOrderProcessFailMessage(order.getId(), "Fail", order.getMember().getEmail());
        }
    }

    @Transactional
    public RsData cancel(Long orderId) {
        Order order = findById(orderId);
        RsData result = validateAvailableCancel(order);
        if (result.isFail()) return result;

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

    public RsData<Order> getOrderFormData(Long orderId) {
        Order order;
        try{
            order = findById(orderId);
            if(order.getPaymentKey() != null || order.getOrderState().equals("주문취소"))
                return RsData.of("F-2", "이미 처리된 주문입니다.");

            if(order.getOrderItems().isEmpty())
                return RsData.of("F-1", "주문 중에 오류가 발생했습니다. 다시 시도해주세요");
        } catch (OrderNotFoundException e){
            return RsData.of("F-1", "주문 중에 오류가 발생했습니다. 다시 시도해주세요");
        }

        return RsData.of("S-1", "유효성 검증 성공", order);
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

    private List<OrderResponse> checkingTempStatus(Page<OrderResponse> findOrders) {
        return findOrders.stream()
                .filter(orderResponse -> !Objects.equals(orderResponse.getOrderStatus(), OrderStatus.TEMP.toString()))
                .collect(Collectors.toList());
    }

    private RsData validateCartToOrder(Member member, CartToOrderRequest request, List<CartItem> cartItems) {
        if(member.getAddress().trim().isEmpty())
            return RsData.of("F-1", "주소를 알 수 없어 주문이 불가능합니다.");

        if(request.getSelectedItems().isEmpty())
            return RsData.of("F-1", "상품을 선택한 후 주문해주세요.");

        if (!Objects.equals(cartItems.get(0).getCart().getMember().getId(), member.getId()))
            return RsData.of("F-1", "현재 접속중인 사용자와 장바구니 사용자가 일치하지 않습니다.");
        return RsData.success();
    }

    private List<OrderItem> orderMaterialsToOrderItems(List<OrderMaterial> orderMaterials) throws ProductStockOutException{
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderMaterial orderMaterial : orderMaterials) { //이때 재고 감소, 재고 체크
            orderItems.add(OrderItem.of(productService.findByIdWithOrder(orderMaterial.getProductId()), orderMaterial.getQuantity(),
                    orderMaterial.getSize(), orderMaterial.getColor()));
        }
        return orderItems;
    }

    private RsData validateAvailableCancel(Order order) {
        if(order.getOrderStatus() == OrderStatus.CANCELED)
            return RsData.of("F-1", "이미 취소된 주문입니다.");

        if (order.getDelivery().getState() == DeliveryState.DELIVERY_ING)
            return RsData.of("F-3", "이미 배송을 시작하여 취소가 불가능합니다.");
        return RsData.success();
    }

    private List<OrderItem> createOrderItems(List<CartItem> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems)
            orderItems.add(OrderItem.of(productService.findByIdWithOrder(cartItem.getProduct().getId()), cartItem));

        return orderItems;
    }

    private List<OrderItem> createOrderItems(Long productId, int quantity, String size, String color) {
        return List.of(OrderItem.of(productService.findByIdWithOrder(productId), quantity, size, color));
    }
}