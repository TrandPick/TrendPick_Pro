package project.trendpick_pro.domain.ask.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.product.entity.Product;

public interface AskRepository extends JpaRepository<Ask, Long>, AskRepositoryCustom {

}
