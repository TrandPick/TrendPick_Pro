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
}