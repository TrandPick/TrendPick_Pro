package project.trendpick_pro.global.jmeter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.product.entity.dto.ProductRequest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/jmeter")
public class JmeterController {

    private final Rq rq;

    private final OrderService orderService;
    private final ProductService productService;

    @GetMapping("/member/login")
    public ResponseEntity<MemberInfoDto> getMemberInfo() {
        Member member = rq.getMember();
        MemberInfoDto memberInfoDto = MemberInfoDto.of(member);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(memberInfoDto, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/order")
    public void processOrder() {
        Member member = rq.getMember();

        Long id = 1L;
        int quantity = 1;
        String size = "80";
        String color = "Teal";

        orderService.productToOrder(member, id, quantity, size, color);
    }

    @PostMapping("/edit")
    public void modifyProduct(@RequestParam("productId") Long productId,
                              @RequestParam("mainFile") MultipartFile mainFile,
                              @RequestParam("subFile") List<MultipartFile> subFiles) throws IOException {
        Product product = productService.findById(productId);

        ProductSaveRequest productSaveRequest = new ProductSaveRequest(
                product.getName(), product.getDescription(),
                product.getMainCategory().getName(), product.getSubCategory().getName(), product.getBrand().getName(),
                product.getTags().stream().map(Tag::getName).toList());

        ProductOptionSaveRequest productOptionSaveRequest = new ProductOptionSaveRequest(
                product.getProductOption().getSizes(), product.getProductOption().getColors(),
                product.getProductOption().getStock(), product.getProductOption().getPrice(), product.getProductOption().getStatus().getText());

        ProductRequest productRequest = new ProductRequest(productSaveRequest, productOptionSaveRequest);
        productService.modify(productId, productRequest, mainFile, subFiles);
    }
}
