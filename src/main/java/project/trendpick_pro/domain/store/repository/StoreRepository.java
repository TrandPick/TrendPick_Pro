package project.trendpick_pro.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.store.entity.Store;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByBrand(String brand);
}
