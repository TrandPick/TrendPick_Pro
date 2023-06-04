package project.trendpick_pro.domain.tags.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.repository.TagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    public void save(String name) {
        tagRepository.save(new Tag(name));
    }

    public void save(Product product, String name) {
        tagRepository.save(new Tag(product, name));
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

}
