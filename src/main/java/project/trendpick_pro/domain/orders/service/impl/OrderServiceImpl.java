package project.trendpick_pro.domain.orders.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.delivery.entity.DeliveryState;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.entity.dto.request.CartToOrderRequest;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderStateResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.exceoption.OrderNotFoundException;
import project.trendpick_pro.domain.orders.repository.OrderItemRepository;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.ProductStatus;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.TagType;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    private final CartService cartService;
    private final ProductService productService;
    private final FavoriteTagService favoriteTagService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public RsData<Order> cartToOrder(Member member, CartToOrderRequest request) {
        if(member.getAddress().trim().length() == 0) {
            return RsData.of("F-1", "주소를 알 수 없어 주문이 불가능합니다.");
        }
        if(request.getSelectedItems().isEmpty()){
            return RsData.of("F-1","상품을 선택한 후 주문해주세요.");
        }

        List<CartItem> cartItems = cartService.currentCartItems(member, request);
        if (!Objects.equals(cartItems.get(0).getCart().getMember().getId(), member.getId())) {
            return RsData.of("F-1", "현재 접속중인 사용자와 장바구니 사용자가 일치하지 않습니다.");
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productService.findById(cartItem.getProduct().getId());
            if (product.getProductOption().getStock() < cartItem.getQuantity()) {
                return RsData.of("F-2", product.getName()+"의 재고가 부족합니다.");
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
            Order saveOrder = orderRepository.save(
                    Order.createOrder(member, new Delivery(member.getAddress()), OrderStatus.TEMP,
                            OrderItem.of(productService.findById(id), quantity, size, color)
                    )
            );
            kafkaTemplate.send("orders", String.valueOf(saveOrder.getId()), String.valueOf(saveOrder.getId()));
            return RsData.of("S-1", "주문을 시작합니다.", saveOrder);
        } catch (ProductNotFoundException e) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
    }

    @Transactional
    public void tryOrder(String Id) throws JsonProcessingException {
        Order order = orderRepository.findById(Long.parseLong(Id)).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
        Member member = order.getMember();

        try {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getProductOption().getStock() == 0) {
                    OutOfStockProduct(orderItem.getProduct());
                    return;
                } else {
                    orderItem.getProduct().getProductOption().decreaseStock(orderItem.getQuantity());
                }
            }
            order.modifyStatus(OrderStatus.ORDERED);
            sendMessage("Success", order.getId(), member.getEmail());
        } finally {
            if (order.getStatus() == OrderStatus.TEMP) {
                order.cancelTemp();
                sendMessage("Fail", order.getId(), member.getEmail());
            }
        }
    }

    @CacheEvict(value = "product", key = "#product.id")
    @Transactional
    public void OutOfStockProduct(Product product) {
        product.getProductOption().connectStatus(ProductStatus.SOLD_OUT);
    }

    private void sendMessage(String message, Long orderId, String email) throws JsonProcessingException {

        OrderStateResponse response = OrderStateResponse.builder()
                .orderId(orderId)
                .message(message)
                .email(email)
                .build();

        String json = objectMapper.writeValueAsString(response);
        kafkaTemplate.send("standByOrder", UUID.randomUUID().toString(), json);
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
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Member member, int offset) {
        Page<OrderResponse> findOrders;
        if (member.getRole().equals(RoleType.MEMBER)) {
            findOrders = orderRepository.findAllByMember(new OrderSearchCond(member.getId()), PageRequest.of(offset, 10));
        } else {
            findOrders = orderRepository.findAllBySeller(new OrderSearchCond(member.getBrand()), PageRequest.of(offset, 10));
        }
        List<OrderResponse> validOrders = findOrders.stream()
                .filter(orderResponse -> !Objects.equals(orderResponse.getOrderStatus(), OrderStatus.TEMP.toString()))
                .collect(Collectors.toList());
        return new PageImpl<>(validOrders, findOrders.getPageable(), validOrders.size());
    }

    @Transactional(readOnly = true)
    public int settlementOfSales(Member member) {
        return orderRepository.findAllByMonth(new OrderSearchCond(member.getBrand()));
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("존재하지 않는 주문입니다."));
    }
    @Transactional(readOnly = true)
    public Page<OrderResponse> findCanceledOrders(Member member, int offset) {
        return orderRepository.findAllByMember(new OrderSearchCond(member.getId(), OrderStatus.CANCELED), PageRequest.of(offset, 10));
    }
    @Transactional(readOnly = true)
    public List<OrderItem> findAllByPayDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderItemRepository.findAllByPayDateBetween(fromDate, toDate);
    }
}
