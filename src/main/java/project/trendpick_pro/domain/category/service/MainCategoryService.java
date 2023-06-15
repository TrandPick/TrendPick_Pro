package project.trendpick_pro.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.dto.response.MainCategoryResponse;
import project.trendpick_pro.domain.category.repository.MainCategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainCategoryService {

    private final MainCategoryRepository mainCategoryRepository;

    @Transactional
    public void save(String name) {
        mainCategoryRepository.save(new MainCategory(name));
    }

    @Transactional
    public void saveAll(List<String> name) {
        List<MainCategory> list = new ArrayList<>();
        for (String s : name) {
            list.add(new MainCategory(s));
        }
        mainCategoryRepository.saveAll(list);
    }

    @Transactional
    public void delete(Long id) {
        MainCategory mainCategory = mainCategoryRepository.findById(id).orElseThrow();
        mainCategoryRepository.delete(mainCategory);
    }

    public List<MainCategoryResponse> findAll() {
        return mainCategoryRepository.findAllBy();
    }

    public MainCategoryResponse findById(Long id) {
        MainCategory mainCategory = mainCategoryRepository.findById(id).orElseThrow();
        return new MainCategoryResponse(mainCategory.getName());
    }

    public MainCategory findByBaseId(Long id) {
        return mainCategoryRepository.findById(id).orElseThrow();
    }

    public MainCategory findByName(String username) {
        return mainCategoryRepository.findByName(username);
    }
}