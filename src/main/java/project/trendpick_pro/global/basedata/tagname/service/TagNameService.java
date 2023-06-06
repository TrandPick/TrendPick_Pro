package project.trendpick_pro.global.basedata.tagname.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.trendpick_pro.global.basedata.tagname.entity.TagName;
import project.trendpick_pro.global.basedata.tagname.entity.dto.TagNameResponse;
import project.trendpick_pro.global.basedata.tagname.repository.TagNameRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagNameService {

    private final TagNameRepository tagNameRepository;

    public void save(String name) {
        tagNameRepository.save(new TagName(name));
    }

    public TagName findByName(String name) {
        return tagNameRepository.findByName(name);
    }

    public List<TagNameResponse> findAll() {
        List<TagName> tags = tagNameRepository.findAllBy();
        return tags.stream().map(tagName -> new TagNameResponse(tagName.getName())).toList();
    }

    public TagName findById(Long id) {
        return tagNameRepository.findById(id).get();
    }
}
