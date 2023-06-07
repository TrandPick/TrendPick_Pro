package project.trendpick_pro.domain.ask.entity.dto.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AskForm {

    private Long productId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
