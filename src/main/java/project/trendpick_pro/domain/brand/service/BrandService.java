package project.trendpick_pro.domain.brand.service;

import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.entity.dto.BrandResponse;

import java.util.List;

public interface BrandService {
    boolean isPresent(String name);
    void save(String name);
    void delete(Long id);
    List<BrandResponse> findAll();
    Brand findByName(String name);
    Brand findById(Long id);
    Long count();
    void saveAll(List<String> brands);
}
