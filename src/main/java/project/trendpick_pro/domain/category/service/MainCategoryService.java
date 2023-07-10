package project.trendpick_pro.domain.category.service;

import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.dto.response.MainCategoryResponse;

import java.util.List;


public interface MainCategoryService {
    void save(String name);
    void saveAll(List<String> name);
    void delete(Long id);
    List<MainCategoryResponse> findAll();
    MainCategoryResponse findById(Long id);
    MainCategory findByBaseId(Long id);
    MainCategory findByName(String username);
}