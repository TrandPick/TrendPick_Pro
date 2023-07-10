package project.trendpick_pro.domain.tags.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.repository.TagRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService{

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public void delete(Set<Tag> tags) {
//        for (Tag tag : tags) {
//            tagRepository.delete(tag);
//        }
        tagRepository.deleteAllInBatch(tags);
    }
}
