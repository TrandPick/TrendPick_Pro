package project.trendpick_pro.global.basedata.tagname.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.global.basedata.tagname.entity.TagName;
import project.trendpick_pro.global.basedata.tagname.repository.TagNameRepository;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagNameServiceImpl implements TagNameService {

    private final TagNameRepository tagNameRepository;

    @Transactional
    @Override
    public void save(String name) {
        tagNameRepository.save(new TagName(name));
    }

    @Transactional
    @Override
    public void saveAll(List<String> name) {
        List<TagName> list = new ArrayList<>();
        for (String s : name) {
            list.add(new TagName(s));
        }
        tagNameRepository.saveAll(list);
    }

    @Transactional(readOnly = true)
    @Override
    public TagName findByName(String name) {
        return tagNameRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findAll() {
        List<TagName> tags = tagNameRepository.findAllBy();
        return tags.stream().map(TagName::getName).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public TagName findById(Long id) {
        return tagNameRepository.findById(id).get();
    }
}