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
import project.trendpick_pro.domain.cart.service.CartService;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderForm;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
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

import java.util.ArrayList;
import java.util.List;

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
        for (OrderItemDto orderItemDto : orderForm.getOrderItems()) {
            Product product = productRepository.findById(orderItemDto.getProductId()).orElseThrow(() ->  new ProductNotFoundException("존재하지 않는 상품 입니다."));
            if (product.getStock() < orderItemDto.getCount()) {
                throw new ProductStockOutException(product.getName()+"의 재고가 부족합니다.");   // 임시. 나중에 사용자 exception 널을까말까 생각
            }

            favoriteTagService.updateTag(member, product, TagType.ORDER);
            orderItemList.add(OrderItem.of(product, orderItemDto));
        }

        Order order = Order.createOrder(member, delivery, OrderStatus.ORDERED, orderItemList, orderForm.getPaymentMethod());
        orderRepository.save(order);
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        order.cancel();
    }

    public Page<OrderResponse> findAllByMember(Member member, int offset) {
        return orderRepository.findAllByMember(new OrderSearchCond(member.getId()), PageRequest.of(offset, 10));
    }

    public OrderForm cartToOrder(Member member, List<Long> selectedItems) {
        List<CartItem> cartItems = cartService.findCartItems(selectedItems);
        for (CartItem cartItem : cartItems) {
            if(cartItem.getCart().getMember().getId() != member.getId())
                throw new MemberNotMatchException("현재 접속중인 사용자와 장바구니 사용자가 일치하지 않습니다.");
        }

        return new OrderForm(MemberInfoDto.of(member) ,convertToOrderItemDto(cartItems));
    }

    private List<OrderItemDto> convertToOrderItemDto(List<CartItem> cartItems) {
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            orderItemDtoList.add(OrderItemDto.of(cartItem.getProduct(), cartItem.getCount()));
        }
        return orderItemDtoList;
    }

}
