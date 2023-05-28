package project.trendpick_pro.domain.answer.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AnswerRequest {

    @NotBlank
    private String content;

}
