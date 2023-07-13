package project.trendpick_pro.global.kafka.view.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.global.kafka.view.entity.View;

public interface ViewRepository extends JpaRepository<View, Long> {
}
