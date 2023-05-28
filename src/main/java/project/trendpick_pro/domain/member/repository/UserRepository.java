package project.trendpick_pro.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.member.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
