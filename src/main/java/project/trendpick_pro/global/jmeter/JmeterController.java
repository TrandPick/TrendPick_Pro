package project.trendpick_pro.global.jmeter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.response.MemberInfoResponse;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.domain.product.entity.dto.ProductRequest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.global.kafka.KafkaProducerService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Profile("dev")
@RequestMapping("/jmeter")
public class JmeterController {

    private final Rq rq;

    private final OrderService orderService;
    private final ProductService productService;

    private final KafkaProducerService kafkaProducerService;

    @GetMapping("/member/login")
    public ResponseEntity<MemberInfoResponse> getMemberInfo() {
        Member member = rq.getMember();
        MemberInfoResponse memberInfoResponse = MemberInfoResponse.of(member);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(memberInfoResponse, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/order")
    @ResponseBody
    public void processOrder() {
        Member member = rq.getMember();

        Long id = 3L;
        int quantity = 1;
        String size = "80";
        String color = "Sliver";

        RsData<Long> data = orderService.productToOrder(member, id, quantity, size, color);
        kafkaProducerService.sendMessage(data.getData());
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

        ProductOptionSaveRequest productOptionSaveRequest = ProductOptionSaveRequest.of(
                product.getProductOption().getSizes(), product.getProductOption().getColors(),
                product.getProductOption().getStock(), product.getProductOption().getPrice(), product.getProductOption().getStatus().getText());

        ProductRequest productRequest = new ProductRequest(productSaveRequest, productOptionSaveRequest);
        productService.modify(productId, productRequest, mainFile, subFiles);
    }
}
