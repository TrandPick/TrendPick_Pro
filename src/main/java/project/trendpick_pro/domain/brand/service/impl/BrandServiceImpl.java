package project.trendpick_pro.domain.brand.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.entity.dto.BrandResponse;
import project.trendpick_pro.domain.brand.repository.BrandRepository;
import project.trendpick_pro.domain.brand.service.BrandService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    
    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    @Override
    public boolean isPresent(String name){
        return brandRepository.findByName(name).isPresent();
    }

    @Transactional
    @Override
    public void save(String name){
        brandRepository.save(new Brand(name));
    }

    @Transactional
    @Override
    public void delete(Long id){
        Brand brand = brandRepository.findById(id).orElseThrow();
        brandRepository.delete(brand);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BrandResponse> findAll(){
        return brandRepository.findAllBy();
    }

    @Transactional(readOnly = true)
    @Override
    public Brand findByName(String name) {
        try {
            return brandRepository.findByName(name).get();
        } catch (Exception e) {
            return brandRepository.save(new Brand(name));
        }
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
}
