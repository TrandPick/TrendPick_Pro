package project.trendpick_pro.global.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.service.ProductService;
//import project.trendpick_pro.global.search.service.SearchService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

//    private final SearchService searchService;
    private final ProductService productService;

//    @GetMapping("/keyword")
//    public String search(@RequestParam String keyword, @RequestParam("page") int offset,  Model model) {
//        Page<ProductListResponse> products = searchService.searchProduct(keyword, offset);
//        model.addAttribute("products", products);
//        model.addAttribute("mainCategoryName",  "search");
//        return "trendpick/products/list";
//    }

    @GetMapping("/keyword")
    public String searchQuery(@RequestParam String keyword, @RequestParam(value = "page", defaultValue = "0") int offset,  Model model) {
        Page<ProductListResponse> products = productService.findAllByKeyword(keyword, offset);
        model.addAttribute("productResponses", products);
        return "trendpick/products/list";
    }
}

