package project.trendpick_pro.global.basedata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.tag.entity.Tag;
import project.trendpick_pro.domain.tag.repository.TagRepository;

import java.util.List;

@Configuration
@Profile({"dev", "test"})
public class BaseData {

    @Value("${tag}")
    List<String> tags;

    @Bean
    CommandLineRunner initData(
            TagRepository tagRepository
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) {
                for (String tag : tags) {
                    tagRepository.save(new Tag(tag));
                }
            }
        };
    }
}
