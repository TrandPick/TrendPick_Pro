package project.trendpick_pro.domain.orders.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.delivery.entity.Delivery;
import project.trendpick_pro.domain.delivery.entity.embaded.Address;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSaveRequest;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderResponse;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.user.entity.User;
import project.trendpick_pro.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void order(Long userId, OrderSaveRequest... orderSaveRequests) throws IllegalAccessException {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저 입니다."));// 임시 exception 설정 나중에 할 수도 있고 아닐수도

        Delivery delivery = new Delivery(Address.of("서울시", "강남대로", "222")); // 임의로 작성

        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderSaveRequest request: orderSaveRequests) {
            Product product = productRepository.findById(request.getProductId()).orElseThrow(() ->  new EntityNotFoundException("product is not found"));
            if (product.getStock() < request.getQuantity()) {
                throw new RuntimeException("stock is not enough");   // 임시. 나중에 사용자 exception 널을까말까 생각
            }
            orderItemList.add(new OrderItem(product, product.getPrice(), request.getQuantity()));
        }

        Order order = Order.createOrder(user, delivery, OrderStatus.ORDERED, orderItemList);
        orderRepository.save(order);
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        order.cancel();
    }

    //임시 작성
    public Page<Order> findAll(OrderSearchCond cond) {
        User user = userRepository.findById(cond.getUserId()).orElseThrow();

        Page<Order> orderPage = orderRepository.findAllByUser(user, PageRequest.of(0, 10));

        return orderPage;
    }


}
