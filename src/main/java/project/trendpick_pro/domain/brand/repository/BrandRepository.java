package project.trendpick_pro.domain.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.entity.dto.BrandResponse;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);

    List<BrandResponse> findAllBy();

}
