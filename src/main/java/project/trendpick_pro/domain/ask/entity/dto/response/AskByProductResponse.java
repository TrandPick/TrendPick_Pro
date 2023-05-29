package project.trendpick_pro.domain.ask.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AskByProductResponse {
    Long id;
    String state;
    String title;
    String username;
    String date;

    @QueryProjection
    public AskByProductResponse(Long id, String state, String title, String username, LocalDateTime localDateTime) {
        this.id = id;
        this.state = state;
        this.title = title;
        this.username = username;
        this.date = localDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
