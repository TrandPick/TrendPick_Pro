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
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSaveRequest;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderSearchCond;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.domain.orders.repository.OrderRepository;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public synchronized void order(Long userId, OrderSaveRequest... orderSaveRequests) {

        Member member = memberRepository.findById(userId).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 유저 입니다."));// 임시 exception 설정 나중에 할 수도 있고 아닐수도

        Delivery delivery = new Delivery(Address.of("서울시", "강남대로", "222")); // 임의로 작성

        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderSaveRequest request: orderSaveRequests) {
            Product product = productRepository.findById(request.getProductId()).orElseThrow(() ->  new ProductNotFoundException("존재하지 않는 상품 입니다."));
            if (product.getStock() < request.getQuantity()) {
                throw new ProductStockOutException("재고가 부족합니다.");   // 임시. 나중에 사용자 exception 널을까말까 생각
            }

            List<Tag> tagList = product.getTags();
            List<Tag> tags = member.getTags();

            //주문은 1유형이라고 가정
            for(Tag tagByProduct : tagList){
                boolean hasTag = false;
                for(Tag tagByMember : tags){
                    if(tagByProduct.getName().equals(tagByMember)){ //기존에 가지고 있던 태그에는 점수 부여
                        tagByMember.increaseScore(1);
                        hasTag = true;
                    }
                }
                if(!hasTag) //태그를 가지고 있지 않다면 추가해준다.
                    tags.add(new Tag(tagByProduct.getName()));
            }

            orderItemList.add(new OrderItem(product, product.getPrice(), request.getQuantity()));
        }





        Order order = Order.createOrder(member, delivery, OrderStatus.ORDERED, orderItemList);
        orderRepository.save(order);
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        order.cancel();
    }

    //임시 작성
    public Page<Order> findAll(OrderSearchCond cond) {
        Member member = memberRepository.findById(cond.getMemberId()).orElseThrow();
        return orderRepository.findAllByMember(member, PageRequest.of(0, 10));
    }


}
