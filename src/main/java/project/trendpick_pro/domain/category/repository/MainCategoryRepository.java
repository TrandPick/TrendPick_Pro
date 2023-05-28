package project.trendpick_pro.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.category.entity.MainCategory;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
    public MainCategory findByName(String name);
}
