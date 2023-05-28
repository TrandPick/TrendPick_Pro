package project.trendpick_pro.domain.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.brand.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    public Brand findByName(String name);
}
