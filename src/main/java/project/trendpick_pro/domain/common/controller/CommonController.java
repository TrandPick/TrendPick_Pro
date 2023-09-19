package project.trendpick_pro.domain.common.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommonController {

    @GetMapping("/")
    public String homePage(HttpSession session) {
        String mainCategory = "전체";
        return "redirect:" + UriComponentsBuilder
                .fromPath("/trendpick/products/list")
                .queryParam("main-category", mainCategory)
                .toUriString();
    }
}
