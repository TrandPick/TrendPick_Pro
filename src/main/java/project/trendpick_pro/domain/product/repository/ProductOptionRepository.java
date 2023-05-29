package project.trendpick_pro.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.product.entity.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
}
