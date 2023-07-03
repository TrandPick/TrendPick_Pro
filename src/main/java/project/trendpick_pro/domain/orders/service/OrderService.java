package project.trendpick_pro.domain.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.delivery.entity.DeliveryState;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.exceoption.OrderNotFoundException;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;
import project.trendpick_pro.global.rsData.RsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CartService cartService;
    private final ProductService productService;
    private final FavoriteTagService favoriteTagService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public RsData<Order> cartToOrder(Member member, List<Long> selectedItems) {

        if(member.getAddress().trim().length() == 0) {
            return RsData.of("F-1", "주소를 알 수 없어 주문이 불가능합니다.");
        }
        if(selectedItems.isEmpty()){
            return RsData.of("F-1","상품을 선택한 후 주문해주세요.");
        }

        List<CartItem> cartItems = cartService.findCartItems(member, selectedItems);
        if (!Objects.equals(cartItems.get(0).getCart().getMember().getId(), member.getId())) {
            return RsData.of("F-1", "현재 접속중인 사용자와 장바구니 사용자가 일치하지 않습니다.");
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productService.findById(cartItem.getProduct().getId());
            if (product.getProductOption().getStock() < cartItem.getQuantity()) {
                RsData.of("F-2", product.getName()+"의 재고가 부족합니다.");
            }
            favoriteTagService.updateTag(member, product, TagType.ORDER);
            orderItemList.add(OrderItem.of(product, cartItem));
        }

        Order order = Order.createOrder(member, new Delivery(member.getAddress()), OrderStatus.TEMP, orderItemList, cartItems);
        Order saveOrder = orderRepository.save(order);

        kafkaTemplate.send("orders", String.valueOf(saveOrder.getId()), String.valueOf(saveOrder.getId()));

        return RsData.of("S-1", "주문을 시작합니다.", saveOrder);
    }

    @Transactional
    public RsData<Order> productToOrder(Member member, Long id, int quantity, String size, String color) {
        try {
            Product product = productService.findById(id);
            OrderItem orderItem = OrderItem.of(product, quantity, size, color);

            Order order = Order.createOrder(member, new Delivery(member.getAddress()), OrderStatus.TEMP, orderItem);
            Order saveOrder = orderRepository.save(order);

            kafkaTemplate.send("orders", String.valueOf(saveOrder.getId()), String.valueOf(saveOrder.getId()));

            return RsData.of("S-1", "주문을 시작합니다.", saveOrder);
        } catch (ProductNotFoundException e) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
    }

    @Transactional
    @KafkaListener(topicPattern = "orders", groupId = "group_id")
    public void orderToOrder(@Payload String Id) throws JsonProcessingException {
        Order order = orderRepository.findById(Long.valueOf(Id)).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
        Member member = order.getMember();
        try {
            for (OrderItem orderItem : order.getOrderItems()) {
                orderItem.getProduct().getProductOption().decreaseStock(orderItem.getQuantity());
            }
            order.modifyStatus(OrderStatus.ORDERED);
            delaySend("Success", order.getId(), member.getEmail());
        } catch (ProductStockOutException e) {
            order.cancelTemp();
            delaySend("Fail", order.getId(), member.getEmail());
        }
    }

    public void delaySend(String message, Long orderId, String email) throws JsonProcessingException {

        OrderStateResponse response = OrderStateResponse.builder()
                .orderId(orderId)
                .message(message)
                .email(email)
                .build();

        String json = objectMapper.writeValueAsString(response);
        kafkaTemplate.send("standByOrder", UUID.randomUUID().toString(), json);
    }

    @KafkaListener(topicPattern = "standByOrder", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void message(@Payload String json) throws JsonProcessingException {
        OrderStateResponse response = objectMapper.readValue(json, OrderStateResponse.class);
        messagingTemplate.convertAndSendToUser(response.getEmail(), "/topic/standByOrder", json);
    }

    @Transactional
    public RsData cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
        if(order.getStatus() == OrderStatus.CANCELED) {
            return RsData.of("F-1", "이미 취소된 주문입니다.");
        } else if (order.getDelivery().getState() == DeliveryState.COMPLETED) {
            return RsData.of("F-2", "이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        if (order.getDelivery().getState() == DeliveryState.DELIVERY_ING) {
            return RsData.of("F-3", "이미 배송을 시작하여 취소가 불가능합니다.");
        }
        order.cancel();
        return RsData.of("S-1", "환불 요청이 정상적으로 진행되었습니다. 환불까지는 최소 2일에서 최대 14일까지 소요될 수 있습니다.");
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
        orderRepository.delete(order);
    }

    public RsData<OrderDetailResponse> showOrderItems(Member member, Long orderId) {
        try {
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
            if (order.getStatus() == OrderStatus.TEMP) {
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

    public Page<OrderResponse> findAllByMember(Member member, int offset) {
        Page<OrderResponse> allByMember = orderRepository.findAllByMember(new OrderSearchCond(member.getId()), PageRequest.of(offset, 10));

        List<OrderResponse> validOrders = new ArrayList<>();
        for (OrderResponse orderResponse : allByMember.getContent()) {
            Order order = orderRepository.findById(orderResponse.getOrderId()).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
            if (order.getStatus() != OrderStatus.TEMP) {
                validOrders.add(orderResponse);
            }
        }

        // 변환
        List<OrderResponse> modifiableList = new ArrayList<>(allByMember.getContent());

        // 삭제 대상인 임시 주문 필터링
        modifiableList.removeIf(orderResponse -> !validOrders.contains(orderResponse));
        return new PageImpl<>(modifiableList, allByMember.getPageable(), modifiableList.size());
    }


    public Page<OrderResponse> findAllBySeller(Member member, int offset) {
        Page<OrderResponse> allBySeller = orderRepository.findAllBySeller(new OrderSearchCond(member.getBrand()), PageRequest.of(offset, 10));

        List<OrderResponse> validOrders = new ArrayList<>();
        for (OrderResponse orderResponse : allBySeller.getContent()) {
            Order order = orderRepository.findById(orderResponse.getOrderId()).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
            if (order.getStatus() != OrderStatus.TEMP) {
                validOrders.add(orderResponse);
            }
        }

        // 변환
        List<OrderResponse> modifiableList = new ArrayList<>(allBySeller.getContent());

        // 삭제 대상인 임시 주문 필터링
        modifiableList.removeIf(orderResponse -> !validOrders.contains(orderResponse));
        return new PageImpl<>(modifiableList, allBySeller.getPageable(), modifiableList.size());
    }

    public int settlementOfSales(Member member) {
        int totalPrice = 0;

        List<OrderResponse> ordersPerMonth = orderRepository.findAllByMonth(new OrderSearchCond(member.getBrand()));

        for(OrderResponse op : ordersPerMonth){
            if(op.getOrderStatus().equals("ORDERED")){
                totalPrice += op.getTotalPrice();
            }
        }

        return totalPrice;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
    }

    public Page<OrderResponse> findCanceledOrders(Member member, int offset) {
        return orderRepository.findAllByMember(new OrderSearchCond(member.getId(), OrderStatus.CANCELED), PageRequest.of(offset, 10));
    }
}