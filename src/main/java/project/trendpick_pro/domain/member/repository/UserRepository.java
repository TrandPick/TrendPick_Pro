package project.trendpick_pro.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.member.entity.Member;

public interface UserRepository extends JpaRepository<Member, Long> {
}
