package project.trendpick_pro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TrendPickProApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrendPickProApplication.class, args);
    }
}
