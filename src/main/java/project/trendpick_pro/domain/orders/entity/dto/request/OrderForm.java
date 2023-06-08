package project.trendpick_pro.domain.orders.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

//주문서
@Getter
@Setter
public class OrderForm {

    @NotBlank
    private MemberInfoDto memberInfo;
    @NotBlank
    private List<OrderItemDto> orderItems;
    @NotBlank
    private String paymentMethod;
    private int paymentPrice;

    public OrderForm(MemberInfoDto memberInfo, List<OrderItemDto> orderItems) {
        this.memberInfo = memberInfo;
        this.orderItems = orderItems;
        for (OrderItemDto orderItem : orderItems) {
            this.paymentPrice += orderItem.getTotalPrice();
        }
    }

    public OrderForm() {
    }

    public String getFormattedPaymentPrice(){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(paymentPrice) +"원";
    }
}
