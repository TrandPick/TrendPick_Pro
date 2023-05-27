package project.trendpick_pro.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.dto.response.ProductResponse;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    @Transactional
    public ProductResponse register(ProductSaveRequest productSaveRequest) {
        Product product = Product.of(productSaveRequest);
        productRepository.save(product);
        return ProductResponse.of(product);
    }

    @Transactional
    public ProductResponse modify(Long productId, ProductSaveRequest productSaveRequest) {

        Product product = productRepository.findById(productId).orElseThrow(null);// 임시. 나중에 테스트
        product.update(productSaveRequest);

        return ProductResponse.of(product);
    }

    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(null);// 임시. 나중에 테스트
        productRepository.delete(product);
    }

    public ProductResponse show(Long product_id) {

        Product product = productRepository.findById(product_id).orElseThrow(null);// 임시. 나중에 테스트

        return ProductResponse.of(product);
    }

    public List<ProductResponse> showAll() {

        List<Product> all = productRepository.findAll();// 임시. 나중에 테스트
        List<ProductResponse> responses = new ArrayList<>();

        for (Product product : all) {
            responses.add(ProductResponse.of(product));
        }

        return responses;
    }

}
