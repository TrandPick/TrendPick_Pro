package project.trendpick_pro.domain.category.entity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CategorySaveRequest {
    private String name;
    private String perent;

    public CategorySaveRequest(String name) {
        this.name = name;
    }

    public CategorySaveRequest(String name, String perent) {
        this.name = name;
        this.perent = perent;
    }
}
