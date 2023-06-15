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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;

    @Transactional
    public void save(String name, MainCategory mainCategory){
        subCategoryRepository.save(new SubCategory(name, mainCategory));
    }

    @Transactional
    public void saveAll(List<String> name, MainCategory mainCategory){
        List<SubCategory> list = new ArrayList<>();
        for (String s : name) {
            list.add(new SubCategory(s, mainCategory));
        }
        subCategoryRepository.saveAll(list);
    }

    @Transactional
    public void delete(Long id){
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow();
        subCategoryRepository.delete(subCategory);
    }
    public List<SubCategoryResponse> findAll(String mainCategoryName) {
        if (mainCategoryName.equals("전체")){
            List<SubCategory> categories = subCategoryRepository.findAllBy();
            return categories.stream().map(subCategory -> new SubCategoryResponse(subCategory.getName())).toList();
        } else {
            List<SubCategory> categories = subCategoryRepository.findAllByMainCategory(mainCategoryName);
            return categories.stream().map(subCategory -> new SubCategoryResponse(subCategory.getName())).toList();
        }
    }

    public SubCategoryResponse findById(Long id){
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow();
        return new SubCategoryResponse(subCategory.getName());
    }

    public SubCategory findByBaseId(Long id, MainCategory mainCategory){
        return subCategoryRepository.findByIdInMainCategory(id, mainCategory);
    }

    public SubCategory findByName(String username){
        return subCategoryRepository.findByName(username);
    }
}