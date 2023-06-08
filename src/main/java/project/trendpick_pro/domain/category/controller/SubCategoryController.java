package project.trendpick_pro.domain.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.entity.dto.response.SubCategoryResponse;
import project.trendpick_pro.domain.category.service.SubCategoryService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/getSubCategories")
    public List<SubCategoryResponse> getSubCategories(@RequestParam String mainCategory) {
        return subCategoryService.findAll(mainCategory);
    }
}
