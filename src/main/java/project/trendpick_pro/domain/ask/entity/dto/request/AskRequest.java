package project.trendpick_pro.domain.ask.entity.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AskRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
