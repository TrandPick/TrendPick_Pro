package project.trendpick_pro.domain.common.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;
import project.trendpick_pro.global.kafka.view.service.ViewService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommonController {

    private final ViewService viewService;

    @GetMapping("/")
    public String homePage(HttpSession session) {
        viewService.requestIncrementViewCount(session);
        String mainCategory = "전체";
        return "redirect:" + UriComponentsBuilder
                .fromPath("/trendpick/products/list")
                .queryParam("main-category", mainCategory)
                .toUriString();
    }
}
