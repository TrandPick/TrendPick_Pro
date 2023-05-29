package project.trendpick_pro.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
}
