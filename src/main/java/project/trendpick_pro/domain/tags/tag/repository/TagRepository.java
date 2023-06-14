package project.trendpick_pro.domain.tags.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    public Optional<Tag> findByName(String name);
}
