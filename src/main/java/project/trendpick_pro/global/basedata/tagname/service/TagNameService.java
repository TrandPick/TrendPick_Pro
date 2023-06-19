package project.trendpick_pro.global.basedata.tagname.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.global.basedata.tagname.entity.TagName;
import project.trendpick_pro.global.basedata.tagname.entity.dto.TagNameResponse;
import project.trendpick_pro.global.basedata.tagname.repository.TagNameRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagNameService {

    private final TagNameRepository tagNameRepository;

    @Transactional
    public void save(String name) {
        tagNameRepository.save(new TagName(name));
    }

    @Transactional
    public void saveAll(List<String> name) {
        List<TagName> list = new ArrayList<>();
        for (String s : name) {
            list.add(new TagName(s));
        }
        tagNameRepository.saveAll(list);
    }

    public TagName findByName(String name) {
        return tagNameRepository.findByName(name);
    }

    public List<TagNameResponse> findAll() {
        List<TagName> tags = tagNameRepository.findAllBy();
        return tags.stream().map(tagName -> new TagNameResponse(tagName.getName())).toList();
    }

    public TagName findById(Long id) {
        TagName tagName = tagNameRepository.findById(id).get();
        return tagName;
    }
}