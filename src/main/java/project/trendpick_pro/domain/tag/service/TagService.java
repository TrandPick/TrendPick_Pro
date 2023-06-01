package project.trendpick_pro.domain.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.tag.entity.dto.response.TagListResponse;
import project.trendpick_pro.domain.tag.entity.dto.response.TagResponse;
import project.trendpick_pro.domain.tag.repository.TagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public TagListResponse getAllTags() {
        return new TagListResponse(tagRepository.findAll());
    }
}
