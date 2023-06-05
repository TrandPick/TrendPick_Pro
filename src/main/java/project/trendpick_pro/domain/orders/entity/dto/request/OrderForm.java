package project.trendpick_pro.domain.orders.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;

import java.util.List;

//주문서
@Getter
@Setter
public class OrderForm {

    private MemberInfoDto memberInfo;
    private List<OrderItemDto> orderItems;
    private String paymentMethod;

    public OrderForm(MemberInfoDto memberInfo, List<OrderItemDto> orderItems) {
        this.memberInfo = memberInfo;
        this.orderItems = orderItems;
    }

    public OrderForm() {
    }
}
