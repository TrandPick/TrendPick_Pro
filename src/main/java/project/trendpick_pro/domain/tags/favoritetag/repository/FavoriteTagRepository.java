package project.trendpick_pro.domain.tags.favoritetag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.member.entity.Member;

import java.util.List;
import java.util.Set;

public interface FavoriteTagRepository extends JpaRepository<FavoriteTag, Long> {
    @Query("select t from FavoriteTag t where t.member = :member")
    Set<FavoriteTag> findAllByMember(@Param("member") Member member);
}
