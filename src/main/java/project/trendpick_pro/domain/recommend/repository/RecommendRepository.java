package project.trendpick_pro.domain.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.recommend.entity.Recommend;

public interface RecommendRepository extends JpaRepository<Recommend, Long>, RecommendRepositoryCustom {


    @Modifying
    @Query("delete from Recommend r where r.member.id=:memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}