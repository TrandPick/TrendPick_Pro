package project.trendpick_pro.domain.orders.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.cart.repository.CartRepository;
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.delivery.entity.DeliveryState;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderForm;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderDetailResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.product.entity.form.ProductOptionForm;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;
import project.trendpick_pro.global.rsData.RsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final FavoriteTagService favoriteTagService;

    @Transactional
    public void order(Member member, OrderForm orderForm) {

        Delivery delivery = new Delivery(orderForm.getMemberInfo().getAddress());

        List<OrderItem> orderItemList = new ArrayList<>();
        Product product = null;
        for (OrderItemDto orderItemDto : orderForm.getOrderItems()) {
            product = productRepository.findById(orderItemDto.getProductId()).orElseThrow(() ->  new ProductNotFoundException("존재하지 않는 상품 입니다."));
            if (product.getStock() < orderItemDto.getCount()) {
                throw new ProductStockOutException(product.getName()+"의 재고가 부족합니다.");   // 임시. 나중에 사용자 exception 널을까말까 생각
            }

            favoriteTagService.updateTag(member, product, TagType.ORDER);
            orderItemList.add(OrderItem.of(product, orderItemDto));
        }

        // 주문상품 장바구니에서 삭제
        List<Long> orderItemIds = new ArrayList<>();
        List<CartItem> cartItems=product.getCartItems();
        if(!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                orderItemIds.add(cartItem.getId());
            }
            cartService.deleteCartItemsByOrder(member, orderItemIds);
        }
        Order order = Order.createOrder(member, delivery, OrderStatus.ORDERED, orderItemList, orderForm.getPaymentMethod());
        orderRepository.save(order);
    }

    @Transactional
    public RsData cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        if(order.getStatus() == OrderStatus.CANCELLED)
            return RsData.of("F-1", "이미 취소된 주문입니다.");

        if (order.getDelivery().getState() != DeliveryState.COMPLETED) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        if (order.getDelivery().getState() != DeliveryState.COMPLETED) {
            throw new IllegalStateException("이미 배송을 시작하여 취소가 불가능합니다.");
        }
        order.cancel();
        return RsData.of("S-1", "환불 요청이 정상적으로 진행되었습니다. 환불까지는 최소 2일에서 최대 14일까지 소요될 수 있습니다.");
    }

    public Page<OrderResponse> findAllByMember(Member member, int offset) {
        return orderRepository.findAllByMember(new OrderSearchCond(member.getId()), PageRequest.of(offset, 10));
    }

    public int OrderSize() {
        return orderRepository.findAll().size();
    }

    public OrderForm cartToOrder(Member member, List<Long> selectedItems) {
        List<CartItem> cartItems = cartService.findCartItems(member, selectedItems);
        for (CartItem cartItem : cartItems) {
            if (cartItem.getCart().getMember().getId() != member.getId())
                throw new MemberNotMatchException("현재 접속중인 사용자와 장바구니 사용자가 일치하지 않습니다.");
        }

        return new OrderForm(MemberInfoDto.of(member) ,convertToOrderItemDto(cartItems));
    }

    private List<OrderItemDto> convertToOrderItemDto(List<CartItem> cartItems) {
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            orderItemDtoList.add(OrderItemDto.of(cartItem.getProduct(), cartItem.getQuantity()));
        }
        return orderItemDtoList;
    }

    public OrderForm productToOrder(Member member, ProductOptionForm productOptionForm) {
        Product product = productRepository.findById(productOptionForm.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        orderItemDtoList.add(OrderItemDto.of(product, productOptionForm.getQuantity()));
        return new OrderForm(MemberInfoDto.of(member), orderItemDtoList);
    }

    public OrderDetailResponse showOrderItems(Member member, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("잘못된 주문번호입니다."));
        if (order.getMember().getId() != member.getId())
            throw new IllegalArgumentException("다른 사용자의 주문에는 접근할 수 없습니다.");
        return OrderDetailResponse.of(order, orderRepository.findOrderItemsByOrderId(orderId));
    }
    public Page<OrderResponse> findAllBySeller(Member member, int offset) {
        return orderRepository.findAllBySeller(
                new OrderSearchCond(member.getBrand()), PageRequest.of(offset, 10));
    }
}