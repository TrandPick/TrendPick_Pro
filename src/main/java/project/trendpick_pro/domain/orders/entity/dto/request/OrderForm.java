package project.trendpick_pro.domain.orders.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.member.entity.dto.response.MemberInfoResponse;
import project.trendpick_pro.domain.orders.entity.dto.response.OrderItemDto;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
public class OrderForm {

    private MemberInfoResponse memberInfo;

    private List<OrderItemDto> orderItems;

    @NotBlank
    private String paymentMethod;
    private int paymentPrice = 0;

    public OrderForm(MemberInfoResponse memberInfo, List<OrderItemDto> orderItems) {
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
}
