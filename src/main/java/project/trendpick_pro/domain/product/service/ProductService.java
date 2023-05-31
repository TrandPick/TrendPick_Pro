package project.trendpick_pro.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.repository.BrandRepository;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.repository.MainCategoryRepository;
import project.trendpick_pro.domain.category.repository.SubCategoryRepository;
import project.trendpick_pro.domain.common.base.filetranslator.FileTranslator;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.dto.response.ProductResponse;
import project.trendpick_pro.domain.product.entity.file.ProductFile;
import project.trendpick_pro.domain.product.repository.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final BrandRepository brandRepository;
    private final FileTranslator fileTranslator;

    @Transactional
    public ProductResponse register(ProductSaveRequest productSaveRequest) throws IOException {

        CommonFile mainFile = fileTranslator.translateFile(productSaveRequest.getMainFile());
        List<CommonFile> subFiles = fileTranslator.translateFileList(productSaveRequest.getSubFiles());

        for(CommonFile subFile : subFiles){
            mainFile.connectFile(subFile);
        }

        MainCategory mainCategory = mainCategoryRepository.findByName(productSaveRequest.getMainCategory());
        SubCategory subCategory = subCategoryRepository.findByName(productSaveRequest.getSubCategory());
        Brand brand = brandRepository.findByName(productSaveRequest.getBrand());

        Product product = Product.of(productSaveRequest, mainCategory, subCategory, brand, mainFile);
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

    public Page<ProductListResponse> showAll(int offset, String mainCategory, String subCategory) {

        List<ProductResponse> responses = new ArrayList<>();

        ProductSearchCond cond = new ProductSearchCond(mainCategory, subCategory);
        PageRequest pageable = PageRequest.of(offset, 18);

        return productRepository.findAllByCategoryId(cond, pageable);
    }

}
