package project.trendpick_pro.domain.ask.entity.dto.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AskForm {

    private Long productId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}