package project.trendpick_pro.domain.category.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.entity.dto.response.SubCategoryResponse;
import project.trendpick_pro.domain.category.repository.SubCategoryRepository;

import java.util.ArrayList;
import java.util.List;

;


public interface SubCategoryService {
    void save(String name, MainCategory mainCategory);
    void saveAll(List<String> name, MainCategory mainCategory);
    void delete(Long id);
    List<SubCategoryResponse> findAll(String mainCategoryName);
    SubCategoryResponse findById(Long id);
    SubCategory findByBaseId(Long id, MainCategory mainCategory);
    SubCategory findByName(String username);
}