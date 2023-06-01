package project.trendpick_pro.domain.tag.entity.dto.response;

import lombok.Getter;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.util.List;

@Getter
public class TagListResponse {

    private final List<String> tags;

    public TagListResponse(List<Tag> tags) {
        this.tags = tags.stream().map(Tag::getName).toList();
    }
}