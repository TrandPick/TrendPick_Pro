package project.trendpick_pro.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.trendpick_pro.domain.category.entity.MainCategory;

import java.util.List;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
    MainCategory findByName(String name);

    @Query("select c.name from MainCategory c")
    List<String> findAllByName();
}
