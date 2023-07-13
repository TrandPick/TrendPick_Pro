package project.trendpick_pro.domain.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.repository.SubCategoryRepository;
import project.trendpick_pro.domain.category.service.SubCategoryService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

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

    public List<String> findAll(String mainCategoryName) {
        List<SubCategory> categories;
        if (mainCategoryName.equals("전체")){
            categories = subCategoryRepository.findAllBy();
        } else {
            categories = subCategoryRepository.findAllByMainCategory(mainCategoryName);
        }
        return categories.stream().map(SubCategory::getName).toList();
    }

    public String findById(Long id){
        return subCategoryRepository.findById(id).orElseThrow().getName();
    }

    public SubCategory findByBaseId(Long id, MainCategory mainCategory){
        return subCategoryRepository.findByIdInMainCategory(id, mainCategory);
    }

    public SubCategory findByName(String username){
        return subCategoryRepository.findByName(username);
    }
}
