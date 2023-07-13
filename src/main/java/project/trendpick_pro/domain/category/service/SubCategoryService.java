package project.trendpick_pro.domain.category.service;

import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;

import java.util.List;

public interface SubCategoryService {
    void save(String name, MainCategory mainCategory);
    void saveAll(List<String> name, MainCategory mainCategory);
    void delete(Long id);
    List<String> findAll(String mainCategoryName);
    String findById(Long id);
    SubCategory findByName(String username);
}