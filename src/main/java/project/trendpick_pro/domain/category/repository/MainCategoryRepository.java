package project.trendpick_pro.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.dto.response.MainCategoryResponse;

import java.util.List;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
    MainCategory findByName(String name);

    List<MainCategoryResponse> findAllBy();
}
