package project.trendpick_pro.global.basedata.tagname.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.global.basedata.tagname.entity.TagName;

import java.util.List;

public interface TagNameRepository extends JpaRepository<TagName, Long> {

    TagName findByName(String tagName);

    List<TagName> findAllBy();
}
