package project.trendpick_pro.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.dto.response.ProductResponse;
import project.trendpick_pro.domain.product.service.ProductService;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/products")
public class ProductController {

    private ProductService productService;

    @PostMapping("/register")
    public String registerProduct(@Valid ProductSaveRequest productSaveRequest, Model model) throws IOException {

        ProductResponse productResponse = productService.register(productSaveRequest);
        model.addAttribute("productResponse", productResponse);

        return "redirect:/trendpick/products/" + productResponse.getId();
    }

    @PostMapping("/edit/{productId}")
    public String modifyProduct(@PathVariable Long productId, @Valid ProductSaveRequest productSaveRequest, Model model) {

        ProductResponse productResponse = productService.modify(productId, productSaveRequest);
        model.addAttribute("productResponse", productResponse);

        return "redirect:/trendpick/products/" + productResponse.getId();
    }

    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId) {
        productService.delete(productId);
        return "redirect:/trendpick/products/list";
    }

    @GetMapping("/{productId}")
    public String showProduct(@PathVariable Long productId, Model model) {
         model.addAttribute("productResponse", productService.show(productId));
        return "/trendpick/products/detailpage";
    }

    @GetMapping("/list")
    public String showAllProduct(@RequestParam("page") int offset, @RequestParam("main-category") String mainCategory,
                                 @RequestParam("sub-category") String subCategory, Model model) {
        model.addAttribute("productResponse", productService.showAll(offset, mainCategory, mainCategory));
        return "/trendpick/products/detailpage";
    }
}
