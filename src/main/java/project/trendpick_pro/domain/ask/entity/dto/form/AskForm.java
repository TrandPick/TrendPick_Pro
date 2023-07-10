package project.trendpick_pro.domain.ask.entity.dto.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AskForm {

    private Long productId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}