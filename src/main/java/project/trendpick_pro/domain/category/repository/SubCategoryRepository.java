package project.trendpick_pro.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.entity.dto.response.SubCategoryResponse;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    public SubCategory findByName(String name);
    List<SubCategoryResponse> findAllBy();
}
