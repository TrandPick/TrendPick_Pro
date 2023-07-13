package project.trendpick_pro.domain.tags.tag.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.repository.TagRepository;
import project.trendpick_pro.domain.tags.tag.service.TagService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Transactional
    @Override
    public void delete(Set<Tag> tags) {
        tagRepository.deleteAllInBatch(tags);
    }
}
