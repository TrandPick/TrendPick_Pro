package project.trendpick_pro.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.dto.response.ProductResponse;
import project.trendpick_pro.domain.product.service.ProductService;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("trendpick/products")
public class ProductController {

    private final Rq rq;
    private final ProductService productService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public String registerProduct(@Valid ProductSaveRequest productSaveRequest, Model model) throws IOException {
        if(!rq.getMember().getRole().equals(RoleType.BRAND_ADMIN))
            throw new RuntimeException("상품 등록 권한이 없습니다.");

        ProductResponse productResponse = productService.register(productSaveRequest);
        model.addAttribute("productResponse", productResponse);

        return "redirect:/trendpick/products/" + productResponse.getId();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/edit/{productId}")
    public String modifyProduct(@PathVariable Long productId, @Valid ProductSaveRequest productSaveRequest, Model model) {
        if(!rq.getMember().getRole().equals(RoleType.BRAND_ADMIN)) //추가로 해당 상품 브랜드 관리자인지도 체크
            throw new RuntimeException("상품 수정 권한이 없습니다.");

        ProductResponse productResponse = productService.modify(productId, productSaveRequest);
        model.addAttribute("productResponse", productResponse);

        return "redirect:/trendpick/products/" + productResponse.getId();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId) {
        if(!rq.getMember().getRole().equals(RoleType.BRAND_ADMIN)) //추가로 해당 상품 브랜드 관리자인지도 체크
            throw new RuntimeException("상품 삭제 권한이 없습니다.");

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
