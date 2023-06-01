package project.trendpick_pro.domain.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.tag.entity.dto.response.TagResponse;
import project.trendpick_pro.domain.tag.repository.TagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<TagResponse> getAllTags() {
//        return TagResponse.of(tagRepository.findAll());
        return null;
    }
}
