package project.trendpick_pro.domain.tags.favoritetag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.member.entity.Member;

import java.util.List;

public interface FavoriteTagRepository extends JpaRepository<FavoriteTag, Long> {
    @Query("select t from FavoriteTag t where t.member = :member")
    List<FavoriteTag> findAllByMember(@Param("member") Member member);
}
