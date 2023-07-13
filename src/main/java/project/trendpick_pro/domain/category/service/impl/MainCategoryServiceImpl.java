package project.trendpick_pro.domain.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.repository.MainCategoryRepository;
import project.trendpick_pro.domain.category.service.MainCategoryService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainCategoryServiceImpl implements MainCategoryService {

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

    public List<String> findAll() {
        return mainCategoryRepository.findAllByName();
    }

    public String findById(Long id) {
        return mainCategoryRepository.findById(id).orElseThrow().getName();
    }

    public MainCategory findByBaseId(Long id) {
        return mainCategoryRepository.findById(id).orElseThrow();
    }

    public MainCategory findByName(String username) {
        return mainCategoryRepository.findByName(username);
    }
}
