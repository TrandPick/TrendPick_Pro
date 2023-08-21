package project.trendpick_pro.domain.withdraw.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithDrawApplyForm {
    @NotBlank
    private String bankName;
    @NotBlank
    private String bankAccountNo;
    @NotNull
    private Integer price;
}