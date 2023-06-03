package project.trendpick_pro.domain.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.entity.dto.BrandResponse;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByName(String name);

    List<BrandResponse> findAllBy();

}
