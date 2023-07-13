package project.trendpick_pro.domain.answer.entity.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AnswerForm {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}