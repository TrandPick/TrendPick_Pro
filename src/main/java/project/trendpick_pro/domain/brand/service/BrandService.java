package project.trendpick_pro.domain.brand.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.entity.dto.BrandResponse;
import project.trendpick_pro.domain.brand.repository.BrandRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    
    private final BrandRepository brandRepository;

    public boolean isPresent(String name){
        return brandRepository.findByName(name).isPresent();
    }

    @Transactional
    public void save(String name){
        brandRepository.save(new Brand(name));
    }

    @Transactional
    public void delete(Long id){
        Brand brand = brandRepository.findById(id).orElseThrow();
        brandRepository.delete(brand);
    }

    public List<BrandResponse> findAll(){
        return brandRepository.findAllBy();
    }

    public Brand findByName(String name) {
        try {
            return brandRepository.findByName(name).get();
        } catch (Exception e) {
            return brandRepository.save(new Brand(name));
        }
    }

    public Brand findById(Long id) {
        return brandRepository.findById(id).orElseThrow();
    }

    public Long count() {
        return brandRepository.count();
    }
}
