package project.trendpick_pro.domain.ask.entity.dto.response;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AskByProductResponse {
    Long id;
    String state;
    String title;
    String username;
    String date;

    public AskByProductResponse(Long id, String state, String title, String username, LocalDateTime localDateTime) {
        this.id = id;
        this.state = state;
        this.title = title;
        this.username = username;
        this.date = localDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
