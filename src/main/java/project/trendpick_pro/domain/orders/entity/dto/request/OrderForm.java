package project.trendpick_pro.domain.orders.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class OrderForm {

    private MemberInfoDto memberInfo;

    private List<OrderItemDto> orderItems;

    @NotBlank
    private String paymentMethod;
    private int paymentPrice = 0;
    public OrderForm(MemberInfoDto memberInfo, List<OrderItemDto> orderItems) {
        this.memberInfo = memberInfo;
        this.orderItems = orderItems;
        for (OrderItemDto orderItem : orderItems) {
            this.paymentPrice += orderItem.getTotalPrice();
        }
    }

    public String getFormattedPaymentPrice(){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(paymentPrice) +" 원";
    }

    @Override
    public String toString() {
        return "OrderForm{" +
                "memberInfo=" + memberInfo +
                ", orderItems=" + orderItems +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentPrice=" + paymentPrice +
                '}';
    }
}
