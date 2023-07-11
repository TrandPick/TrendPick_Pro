package project.trendpick_pro.domain.ask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.ask.entity.Ask;

public interface AskRepository extends JpaRepository<Ask, Long>, AskRepositoryCustom {
}