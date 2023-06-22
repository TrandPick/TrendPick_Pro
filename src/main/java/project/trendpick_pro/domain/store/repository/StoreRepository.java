package project.trendpick_pro.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {

}
