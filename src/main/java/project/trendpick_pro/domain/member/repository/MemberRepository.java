package project.trendpick_pro.domain.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
    Page<Member> findAllByRoleAndRecentlyAccessDateBetween(RoleType role, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}