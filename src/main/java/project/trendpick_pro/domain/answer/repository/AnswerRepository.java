package project.trendpick_pro.domain.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.answer.entity.Answer;
import project.trendpick_pro.domain.ask.entity.Ask;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("select a from Answer a where a.ask = :ask")
    List<Answer> findAllByAskId(@Param("ask") Ask ask);

}