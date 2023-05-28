package project.trendpick_pro.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
