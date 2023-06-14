package project.trendpick_pro.domain.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import project.trendpick_pro.domain.common.base.filetranslator.FileTranslator;

import java.net.MalformedURLException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommonController {

    private final FileTranslator fileTranslator;

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileTranslator.getFilePath(filename));
    }

    @GetMapping("/")
    public String index() {
        String mainCategory = "전체";
        String redirectUrl = UriComponentsBuilder
                .fromPath("/trendpick/products/list")
                .queryParam("main-category", mainCategory)
                .toUriString();

        return "redirect:" + redirectUrl;
    }
}
