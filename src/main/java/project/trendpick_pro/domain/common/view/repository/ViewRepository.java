package project.trendpick_pro.domain.common.view.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.common.view.entity.View;

public interface ViewRepository extends JpaRepository<View, Long> {
}
