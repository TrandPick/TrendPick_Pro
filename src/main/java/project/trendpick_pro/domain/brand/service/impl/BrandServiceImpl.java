package project.trendpick_pro.domain.brand.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.entity.dto.BrandResponse;
import project.trendpick_pro.domain.brand.repository.BrandRepository;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;
import project.trendpick_pro.global.basedata.tagname.entity.TagName;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    @Override
    public boolean isPresent(String name) {
        return brandRepository.findByName(name).isPresent();
    }

    @Transactional
    @Override
    public void save(String name) {
        brandRepository.save(new Brand(name));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow();
        brandRepository.delete(brand);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BrandResponse> findAll() {
        return brandRepository.findAllBy();
    }

    @Transactional(readOnly = true)
    @Override
    public Brand findByName(String name) {
        return brandRepository.findByName(name).orElseThrow(
                () -> new IllegalArgumentException("해당 브랜드는 존재하지 않습니다.")
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Brand findById(Long id) {
        return brandRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    @Override
    public Long count() {
        return brandRepository.count();
    }

    @Override
    @Transactional
    public void saveAll(List<String> brands) {
        List<Brand> list = new ArrayList<>();
        for (String brand : brands) {
            list.add(new Brand(brand));
        }

        brandRepository.saveAll(list);
    }
}
