package project.trendpick_pro.domain.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.recommend.entity.Recommend;

public interface RecommendRepository extends JpaRepository<Recommend, Long>, RecommendRepositoryCustom {

    void deleteAllByMemberId(Long memberId);
}
