package project.trendpick_pro.domain.tags.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    public Optional<Tag> findByName(String name);

    @Query("select t from Tag t where t.product = :product")
    List<Tag> findAllByProduct(@Param("product") Product product);
}
