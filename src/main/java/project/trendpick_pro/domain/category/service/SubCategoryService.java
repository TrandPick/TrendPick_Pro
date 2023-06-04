package project.trendpick_pro.domain.category.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.entity.dto.request.CategorySaveRequest;
import project.trendpick_pro.domain.category.entity.dto.response.SubCategoryResponse;;
import project.trendpick_pro.domain.category.repository.SubCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;

    private final MainCategoryService mainCategoryService;

    @Transactional
    public void save(String name, MainCategory mainCategory){
        subCategoryRepository.save(new SubCategory(name, mainCategory));
    }

    @Transactional
    public void delete(Long id){
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow();
        subCategoryRepository.delete(subCategory);
    }
    public List<SubCategoryResponse> findAll() {
        return subCategoryRepository.findAllBy();
    }

    public SubCategoryResponse findById(Long id){
        SubCategory subCategory=subCategoryRepository.findById(id).orElseThrow();
        return new SubCategoryResponse(subCategory.getName());
    }

    public SubCategory findByName(String username){
        return subCategoryRepository.findByName(username);
    }
}
