package project.trendpick_pro.global.basedata.tagname.service;

import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.global.basedata.tagname.entity.TagName;

import java.util.ArrayList;
import java.util.List;

public interface TagNameService {
    public void save(String name);
    public void saveAll(List<String> name);
    public TagName findByName(String name);
    public List<String> findAll();
    public TagName findById(Long id);
}
