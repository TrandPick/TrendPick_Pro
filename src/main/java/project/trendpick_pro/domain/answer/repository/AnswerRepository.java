package project.trendpick_pro.domain.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.answer.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
