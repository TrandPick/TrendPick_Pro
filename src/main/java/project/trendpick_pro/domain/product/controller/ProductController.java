package project.trendpick_pro.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.category.service.MainCategoryService;
import project.trendpick_pro.domain.category.service.SubCategoryService;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.dto.response.ProductResponse;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.recommend.service.RecommendService;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/products")
public class ProductController {

    private final ProductService productService;
    private final RecommendService recommendService;

    private final TagNameService tagNameService;
    private final BrandService brandService;

    private final MainCategoryService mainCategoryService;
    private final SubCategoryService subCategoryService;

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @GetMapping("/register")
    public String registerProduct(ProductSaveRequest productSaveRequest, @RequestParam("main-category") String mainCategory, Model model) {
        model.addAttribute("productSaveRequest", productSaveRequest);
        model.addAttribute("tags", tagNameService.findAll());
        model.addAttribute("mainCategories", mainCategoryService.findAll());
        model.addAttribute("subCategories", subCategoryService.findAll(mainCategory));
        model.addAttribute("brands", brandService.findAll());
        return "/trendpick/products/register";
    }

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @PostMapping("/register")
    public String register(@ModelAttribute @Valid ProductSaveRequest productSaveRequest,
                           @RequestParam("mainFile") MultipartFile mainFile,
                           @RequestParam("subFiles") List<MultipartFile> subFiles) throws IOException {
        log.info("registerProduct: {}", productSaveRequest.toString());
        Long id = productService.register(productSaveRequest, mainFile, subFiles);
        return "redirect:/trendpick/products/" + id;
    }

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @PostMapping("/edit/{productId}")
    public String modifyProduct(@PathVariable Long productId, @Valid ProductSaveRequest productSaveRequest, @RequestParam("mainFile") MultipartFile mainFile,
                                @RequestParam("subFiles") List<MultipartFile> subFiles) throws IOException {
        Long id = productService.modify(productId, productSaveRequest, mainFile, subFiles);
        return "redirect:/trendpick/products/" + id;
    }

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId) {
        productService.delete(productId);
        return "redirect:/trendpick/products/list";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/{productId}")
    public String showProduct(@PathVariable Long productId, Model model) {
        model.addAttribute("productResponse", productService.show(productId));
        return "/trendpick/products/detailpage";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/list")
    public String showAllProduct(@RequestParam(value = "page", defaultValue = "0") int offset,
                                 @RequestParam(value = "main-category", defaultValue = "추천") String mainCategory,
                                 @RequestParam(value = "sub-category", defaultValue = "전체") String subCategory,
                                 @RequestParam(value = "sort", defaultValue = "1") Integer sortCode,
                                 Model model) {
        if (mainCategory.equals("추천")) {
            model.addAttribute("productResponses", recommendService.getFindAll(offset));
        } else {
            model.addAttribute("productResponses", productService.showAll(offset, mainCategory, subCategory, sortCode));
        } return "/trendpick/products/list";
    }
}
