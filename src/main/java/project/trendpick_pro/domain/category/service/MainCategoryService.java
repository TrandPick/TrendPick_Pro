package project.trendpick_pro.domain.category.service;

import project.trendpick_pro.domain.category.entity.MainCategory;

import java.util.List;

public interface MainCategoryService {
    void save(String name);
    void saveAll(List<String> name);
    void delete(Long id);
    List<String> findAll();
    String findById(Long id);
    MainCategory findByBaseId(Long id);
    MainCategory findByName(String username);
}