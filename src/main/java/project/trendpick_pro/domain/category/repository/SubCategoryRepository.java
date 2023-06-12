package project.trendpick_pro.domain.category.repository;

import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.entity.dto.response.SubCategoryResponse;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    SubCategory findByName(String name);

    @Query("select c from SubCategory c where c.mainCategory.name=:mainCategory")
    List<SubCategory> findAllByMainCategory(@Param("mainCategory") String mainCategory);
    List<SubCategory> findAllBy();

    @Query("select c from SubCategory c where c.id=:id and c.mainCategory=:mainCategory")
    SubCategory findByIdInMainCategory(@Param("id") Long id, @Param("mainCategory") MainCategory mainCategory);
}
