package project.trendpick_pro.domain.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.service.AskService;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.category.entity.dto.response.MainCategoryResponse;
import project.trendpick_pro.domain.category.entity.dto.response.SubCategoryResponse;
import project.trendpick_pro.domain.category.service.MainCategoryService;
import project.trendpick_pro.domain.category.service.SubCategoryService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponseBySeller;
import project.trendpick_pro.domain.product.entity.form.ProductOptionForm;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.recommend.service.RecommendService;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewProductResponse;
import project.trendpick_pro.domain.review.service.ReviewService;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;
import project.trendpick_pro.global.rsData.RsData;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    private final ReviewService reviewService;
    private final AskService askService;

    private final Rq rq;

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @GetMapping("/register")
    public String registerProduct(@ModelAttribute("productSaveRequest") ProductSaveRequest productSaveRequest, Model model) {
        model.addAttribute("tags", tagNameService.findAll());

        List<MainCategoryResponse> MainCategories = mainCategoryService.findAll();
        model.addAttribute("mainCategories", MainCategories);

        Map<String, List<SubCategoryResponse>> subCategoryList = new HashMap<>();
        for (MainCategoryResponse mainCategoryResponse : MainCategories) {
            subCategoryList.put(mainCategoryResponse.getName(), subCategoryService.findAll(mainCategoryResponse.getName()));
        }

        model.addAttribute("subCategoriesList", subCategoryList);
        model.addAttribute("brands", brandService.findByName(rq.CheckAdmin().get().getBrand()));
        return "/trendpick/products/register";
    }

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @PostMapping("/register")
    public String register(@ModelAttribute @Valid ProductSaveRequest productSaveRequest,
                           @RequestParam("mainFile") MultipartFile mainFile,
                           @RequestParam("subFiles") List<MultipartFile> subFiles) throws IOException {
        log.info("registerProduct: {}", productSaveRequest.toString());
        RsData<Long> id = productService.register(productSaveRequest, mainFile, subFiles);
        return rq.redirectWithMsg("/trendpick/products/" + id.getData(), id.getMsg());
    }

    @GetMapping("/edit/{productId}")
    public String modifyBefore(@PathVariable Long productId, Model model) throws IOException {
        model.addAttribute("tags", tagNameService.findAll());

        List<MainCategoryResponse> MainCategories = mainCategoryService.findAll();
        model.addAttribute("mainCategories", MainCategories);

        Map<String, List<SubCategoryResponse>> subCategoryList = new HashMap<>();
        for (MainCategoryResponse mainCategoryResponse : MainCategories) {
            subCategoryList.put(mainCategoryResponse.getName(), subCategoryService.findAll(mainCategoryResponse.getName()));
        }

        model.addAttribute("subCategoriesList", subCategoryList);
        model.addAttribute("brands", brandService.findAll());

        Product product = productService.findById(productId);
        model.addAttribute("originProduct", product);
        return "/trendpick/products/modify";
    }

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @PostMapping("/edit/{productId}")
    public String modifyProduct(@PathVariable Long productId, @Valid ProductSaveRequest productSaveRequest, @RequestParam("mainFile") MultipartFile mainFile,
                                @RequestParam("subFiles") List<MultipartFile> subFiles) throws IOException {
        RsData<Long> id = productService.modify(productId, productSaveRequest, mainFile, subFiles);
        return rq.redirectWithMsg("/trendpick/products/" + id.getData(), id.getMsg());
    }

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId) {
        productService.delete(productId);
        String category = URLEncoder.encode("상의", StandardCharsets.UTF_8);
        return "redirect:/trendpick/products/list?main-category=" + category;

    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{productId}")
    public String showProduct(@PathVariable Long productId, Pageable pageable, ProductOptionForm productOptionForm, Model model) {
        model.addAttribute("productResponse", productService.show(productId));
        model.addAttribute("ProductOptionForm", productOptionForm);
        model.addAttribute("productOptionForm", new ProductOptionForm());
        Page<ReviewProductResponse> productReviews = reviewService.getProductReviews(productId, pageable);
        Page<AskResponse> productAsk = askService.showAsksByProduct(productId, 0);
        model.addAttribute("productReview", productReviews);
        model.addAttribute("productAsk", productAsk);
        return "/trendpick/products/detailpage";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/list")
    public String showAllProduct(@RequestParam(value = "page", defaultValue = "0") int offset,
                                 @RequestParam(value = "main-category", defaultValue = "top") String mainCategory,
                                 @RequestParam(value = "sub-category", defaultValue = "전체") String subCategory,
                                 Pageable pageable, Model model) {
        if (mainCategory.equals("recommend")) {
            mainCategory = "추천";
        } else if (mainCategory.equals("top")) {
            mainCategory = "상의";
        }
        if (mainCategory.equals("추천")) {
            Member member = rq.CheckLogin().orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
            if (member.getRole().getValue().equals("MEMBER")) {
                model.addAttribute("subCategoryName", subCategory);
                model.addAttribute("mainCategoryName", mainCategory);
                model.addAttribute("productResponses", recommendService.getFindAll(member, offset));
                model.addAttribute("subCategories", subCategoryService.findAll(mainCategory));
            } else {
                model.addAttribute("subCategoryName", subCategory);
                model.addAttribute("mainCategoryName", mainCategory);
                model.addAttribute("productResponses", productService.showAll(offset, mainCategory, subCategory));
                model.addAttribute("subCategories", subCategoryService.findAll(mainCategory));
            }
        } else if(mainCategory.equals("전체")){
            model.addAttribute("subCategoryName", subCategory);
            model.addAttribute("mainCategoryName", mainCategory);
            model.addAttribute("productResponses", productService.getAllProducts(pageable));
        } else {
            model.addAttribute("subCategoryName", subCategory);
            model.addAttribute("mainCategoryName", mainCategory);
            model.addAttribute("productResponses", productService.showAll(offset, mainCategory, subCategory));
            model.addAttribute("subCategories", subCategoryService.findAll(mainCategory));
        }
        return "/trendpick/products/list";
    }

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @GetMapping("admin/list")
    public String showAllProductBySeller(@RequestParam("page") int offset, Model model) {
        Page<ProductListResponseBySeller> products =
                productService.findProductsBySeller(rq.CheckAdmin().get(), offset).getData();
        model.addAttribute("products", products);
        return "/trendpick/admin/products";
    }
}
// @RequestParam(value = "sort", defaultValue = "1"), Integer sortCode