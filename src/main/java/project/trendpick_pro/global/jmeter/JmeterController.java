package project.trendpick_pro.global.jmeter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.service.ProductService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/jmeter")
public class JmeterController {

    private final Rq rq;

    private final ProductService productService;

    @GetMapping("/member/login")
    public ResponseEntity<MemberInfoDto> getMemberInfo() {
        Member member = rq.getMember();
        MemberInfoDto memberInfoDto = MemberInfoDto.of(member);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(memberInfoDto, headers, HttpStatus.OK);
    }

//    @PreAuthorize("hasAuthority({'MEMBER'})")
//    @PostMapping("/order")
//    public OrderDetailResponse processOrder() {
//        Member member = rq.CheckMember().get();
//        Product product = productRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
//
//        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
//        orderItemDtoList.add(OrderItemDto.of(product, 1));
//        OrderForm orderForm = new OrderForm(MemberInfoDto.of(member), orderItemDtoList);
//        orderForm.connectPaymentMethod("신용카드");
//
//        RsData<Long> result = orderService.order(member, orderForm);
//        if(result.isFail())
//            throw new IllegalArgumentException(result.getMsg());
//
//        return orderService.showOrderItems(member, result.getData()).getData();
//    }

//    @PostMapping("/edit")
//    public void modifyProduct(@RequestParam("productId") Long productId,
//                              @RequestParam("mainFile") MultipartFile mainFile,
//                              @RequestParam("subFile") List<MultipartFile> subFiles) throws IOException {
//        log.info("productId : {}", productId);
//        ProductSaveRequest productSaveRequest = new ProductSaveRequest("제목", "내용", "상의", "반소매티셔츠", "나이키", 50, 1000, List.of("오버핏청바지", "시티보이룩"));
//        Long id =  productService.modify(productId, productSaveRequest, mainFile, subFiles).getData();
//        log.info("id : {}", id);
//    }
}
