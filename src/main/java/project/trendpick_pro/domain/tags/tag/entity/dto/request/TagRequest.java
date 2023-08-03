package project.trendpick_pro.domain.tags.tag.entity.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class TagRequest {

    List<String> tags;

    public TagRequest(List<String> tags) {
        this.tags = tags;
    }
}
