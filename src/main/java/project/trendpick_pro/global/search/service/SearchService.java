//package project.trendpick_pro.global.search.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import project.trendpick_pro.domain.product.entity.product.Product;
//import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
//import project.trendpick_pro.global.search.entity.ProductSearch;
//import project.trendpick_pro.global.search.exception.DocumentNotFoundException;
//import project.trendpick_pro.global.search.repository.ProductSearchRepository;
//
//@Service
//@RequiredArgsConstructor
//public class SearchService {
//
//    private final ProductSearchRepository productSearchRepository;
//
//    // Create Product
//    public void createProduct(Product product) {
//        productSearchRepository.save(ProductSearch.of(product));
//    }
//
//    // Update Product
//    public void modifyProduct(Product product) {
//        ProductSearch findDocument = findById(product.getId());
//        findDocument.modify(product);
//    }
//
//    // Delete Product
//    public void deleteProduct(Product product) {
//        ProductSearch findDocument = findById(product.getId());
//        productSearchRepository.delete(findDocument);
//    }
//
//    public Page<ProductListResponse> searchProduct(String keyword, int offset) {
//        PageRequest pageable = PageRequest.of(offset, 18);
//        Page<ProductSearch> products = productSearchRepository.findByNameContainingOrBrandContaining(keyword, keyword, pageable);
//        return products.map(ProductListResponse::of);
//    }
//
//    // Helper method to find product document by id
//    public ProductSearch findById(Long id) {
//        return productSearchRepository.findById(String.valueOf(id))
//                .orElseThrow(() -> new DocumentNotFoundException("존재하지 않는 검색 객체입니다."));
//    }
//}
//
