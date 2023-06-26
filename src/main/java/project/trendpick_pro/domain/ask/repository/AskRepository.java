package project.trendpick_pro.domain.ask.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.member.entity.Member;

public interface AskRepository extends JpaRepository<Ask, Long>, AskRepositoryCustom {
    @Query("select a from Ask a where a.author = :member")
    Page<Ask> findAllByMember(Member member, Pageable pageable);
}