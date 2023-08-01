package project.trendpick_pro.domain.product.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.service.MainCategoryService;
import project.trendpick_pro.domain.category.service.SubCategoryService;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.common.file.FileTranslator;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.dto.ProductRequest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.ProductStatus;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductByRecommended;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponseBySeller;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductResponse;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.entity.TagType;
import project.trendpick_pro.domain.tags.tag.service.TagService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final MainCategoryService mainCategoryService;
    private final SubCategoryService subCategoryService;
    private final BrandService brandService;

    private final FileTranslator fileTranslator;
    private final FavoriteTagService favoriteTagService;
    private final TagService tagService;

    private final Rq rq;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public RsData<Long> register(ProductRequest request, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException, ExecutionException, InterruptedException {

        rq.checkAdmin();

        ProductSaveRequest productSaveRequest = request.getRequest1();
        ProductOptionSaveRequest optionSaveRequest = request.getRequest2();

        CommonFile mainFile = fileTranslator.saveFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.saveFiles(requestSubFiles);

        for (CommonFile subFile : subFiles) {
            mainFile.connectFile(subFile);
        }

        Set<Tag> tags = new LinkedHashSet<>();
        for (String tagName : productSaveRequest.getTags()) {
            tags.add(new Tag(tagName));
        }

        CompletableFuture<MainCategory> mainCategoryFuture = CompletableFuture.supplyAsync(() -> mainCategoryService.findByName(productSaveRequest.getMainCategory()));
        CompletableFuture<SubCategory> subCategoryFuture = CompletableFuture.supplyAsync(() -> subCategoryService.findByName(productSaveRequest.getSubCategory()));
        CompletableFuture<Brand> brandFuture = CompletableFuture.supplyAsync(() -> brandService.findByName(productSaveRequest.getBrand()));

        CompletableFuture.allOf(mainCategoryFuture, subCategoryFuture, brandFuture).join();

        MainCategory mainCategory = mainCategoryFuture.get();
        SubCategory subCategory = subCategoryFuture.get();
        Brand brand = brandFuture.get();

        ProductOption productOption = ProductOption.of(optionSaveRequest);
        productOption.settingConnection(brand, mainCategory, subCategory, mainFile, ProductStatus.SALE);
        Product product = Product.of(productSaveRequest.getName(), productSaveRequest.getDescription());
        product.updateTags(tags);

        Product saveProduct = productRepository.save(product);
        return RsData.of("S-1", "상품 등록이 완료되었습니다.", saveProduct.getId());
    }

    @Transactional
    public RsData<Long> modify(Long productId, ProductRequest productRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException {

        rq.checkAdmin();

        ProductSaveRequest productSaveRequest = productRequest.getRequest1();
        ProductOptionSaveRequest optionSaveRequest = productRequest.getRequest2();

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
        CommonFile mainFile = fileTranslator.saveFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.saveFiles(requestSubFiles);

        for (CommonFile subFile : subFiles) {
            mainFile.connectFile(subFile);
        }
        product.getProductOption().getFile().deleteFile(amazonS3, bucket);
        product.getProductOption().updateFile(mainFile);

        Set<Tag> tags = new LinkedHashSet<>();
        for (String tagName : productSaveRequest.getTags()) {
            tags.add(new Tag(tagName));
        }

        tagService.delete(product.getTags());
        product.updateTags(tags);
        product.update(productSaveRequest, optionSaveRequest);
        return RsData.of("S-1", "상품 수정 완료되었습니다.", product.getId());
    }

//    @Caching(evict = {
//            @CacheEvict(value = "product", key = "#productId"),
//            @CacheEvict(value = "productList", allEntries = true)
//    })
    @Transactional
    public void delete(Long productId) {
        rq.getAdmin();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
        product.getProductOption().getFile().deleteFile(amazonS3, bucket);
        productRepository.delete(product);
    }

//    @Cacheable(value = "product", key = "#productId")
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
        if(rq.checkLogin() && rq.checkMember()){
            favoriteTagService.updateTag(rq.getMember(), product, TagType.SHOW);
        }
        return ProductResponse.of(product);
    }

//    @Cacheable(value = "productList", key = "#offset")
    public Page<ProductListResponse> getProducts(int offset, String mainCategory, String subCategory) {
        ProductSearchCond cond = new ProductSearchCond(mainCategory, subCategory);
        PageRequest pageable = PageRequest.of(offset, 18);
        Page<ProductListResponse> allByCategoryId = productRepository.findAllByCategoryId(cond, pageable);
        return allByCategoryId;
    }

    public Page<ProductListResponse> findAllByKeyword(String keyword, int offset) {
        ProductSearchCond cond = new ProductSearchCond(keyword);
        PageRequest pageable = PageRequest.of(offset, 18);
        return productRepository.findAllByKeyword(cond, pageable);
    }

    public List<Product> getRecommendProduct(Member member) {
        List<ProductByRecommended> tags = productRepository.findRecommendProduct(member.getEmail());

        Map<String, List<Long>> productIdListByTagName = new HashMap<>();
        for (ProductByRecommended tag : tags) {
            if (!productIdListByTagName.containsKey(tag.getTagName()))
                productIdListByTagName.put(tag.getTagName(), new ArrayList<>());
            productIdListByTagName.get(tag.getTagName()).add(tag.getProductId());
        }

        Map<Long, ProductByRecommended> recommendProductByProductId = new HashMap<>();
        for (ProductByRecommended response : tags) {
            if (recommendProductByProductId.containsKey(response.getProductId()))
                continue;
            recommendProductByProductId.put(response.getProductId(), response);
        }

        for (FavoriteTag memberTag : member.getTags()) {
            if (productIdListByTagName.containsKey(memberTag.getName())) {
                List<Long> productIdList = productIdListByTagName.get(memberTag.getName());
                for (Long id : productIdList) {
                    recommendProductByProductId.get(id).plusTotalScore(memberTag.getScore());
                }
            }
        }

        List<ProductByRecommended> recommendProductList = new ArrayList<>(recommendProductByProductId.values()).stream()
                .sorted(Comparator.comparing(ProductByRecommended::getTotalScore).reversed())
                .toList();

        List<Product> products = new ArrayList<>();
        for (ProductByRecommended recommendProduct : recommendProductList) {
            products.add(productRepository.findById(recommendProduct.getProductId()).orElseThrow(
                    () -> new ProductNotFoundException("존재하지 않는 상품입니다.")
            ));
        }
        return products;
    }

    public RsData<Page<ProductListResponseBySeller>> findProductsBySeller(Member member, int offset) {
        if (member.getBrand() == null)
            RsData.of("F-1", "브랜드 관리자의 브랜드를 알 수 없습니다. 브랜드를 설정하세요.");

        Pageable pageable = PageRequest.of(offset, 20);
        return RsData.of("S-1", "성공", productRepository.findAllBySeller(member.getBrand(), pageable));
    }

    @Transactional
    public void applyDiscount(Long productId, double discountRate) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
        product.applyDiscount(discountRate);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
    }

    public Product findByIdWithBrand(Long productId) {
        return productRepository.findByIdWithBrand(productId);
    }


}