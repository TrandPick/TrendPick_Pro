package project.trendpick_pro.domain.favoritetag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.favoritetag.entity.FavoriteTag;

public interface FavoriteTagRepository extends JpaRepository<FavoriteTag, Long> {
}
