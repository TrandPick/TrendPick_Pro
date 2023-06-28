package project.trendpick_pro.domain.product.service;


import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import project.trendpick_pro.domain.common.base.filetranslator.FileTranslator;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.product.entity.dto.ProductRequest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.product.dto.response.*;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;
import project.trendpick_pro.domain.tags.tag.service.TagService;
import project.trendpick_pro.global.rsData.RsData;
//import project.trendpick_pro.global.search.service.SearchService;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final MainCategoryService mainCategoryService;
    private final SubCategoryService subCategoryService;
    private final BrandService brandService;

    private final FileTranslator fileTranslator;
    private final FavoriteTagService favoriteTagService;
    private final TagService tagService;

    private final Rq rq;
//    private final SearchService searchService;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("https://kr.object.ncloudstorage.com/{cloud.aws.s3.bucket}/")
    private String filePath;

    @Transactional
    public RsData<Long> register(ProductRequest request, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException {

        rq.checkAdmin();

        ProductSaveRequest productSaveRequest = request.getRequest1();
        ProductOptionSaveRequest optionSaveRequest = request.getRequest2();

        CommonFile mainFile = fileTranslator.translateFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.translateFileList(requestSubFiles);

        for (CommonFile subFile : subFiles) {
            mainFile.connectFile(subFile);
        }

        Set<Tag> tags = new LinkedHashSet<>();
        for (String tagName : productSaveRequest.getTags()) {
            tags.add(new Tag(tagName));
        }

        MainCategory mainCategory = mainCategoryService.findByName(productSaveRequest.getMainCategory());
        SubCategory subCategory = subCategoryService.findByName(productSaveRequest.getSubCategory());
        Brand brand = brandService.findByName(productSaveRequest.getBrand());

        ProductOption productOption = ProductOption.of(optionSaveRequest);
        Product product = Product.of(productSaveRequest, mainCategory, subCategory, brand, mainFile, productOption);
        product.addTag(tags);

        Product saveProduct = productRepository.save(product);
//        searchService.createProduct(saveProduct);
        return RsData.of("S-1", "상품 등록이 완료되었습니다.", saveProduct.getId());
    }

    @Transactional
    public RsData<Long> modify(Long productId, ProductRequest productRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException {

        rq.checkAdmin();

        ProductSaveRequest productSaveRequest = productRequest.getRequest1();
        ProductOptionSaveRequest optionSaveRequest = productRequest.getRequest2();

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        fileTranslator.deleteFile(product.getFile());
        product.disconnectFile();

        CommonFile mainFile = fileTranslator.translateFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.translateFileList(requestSubFiles);

        for (CommonFile subFile : subFiles) {
            mainFile.connectFile(subFile);
        }

        Set<Tag> tags = new LinkedHashSet<>();
        for (String tagName : productSaveRequest.getTags()) {
            tags.add(new Tag(tagName));
        }

        tagService.delete(product.getTags());
        product.modifyTag(tags);
        product.update(productSaveRequest, optionSaveRequest, mainFile);
//        searchService.modifyProduct(product);
        return RsData.of("S-1", "상품 수정 완료되었습니다.", product.getId());
    }

    @Transactional
    public void delete(Long productId) {
        rq.getAdmin();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
        fileTranslator.deleteFile(product.getFile());
//        searchService.deleteProduct(product);
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse getProduct(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        if(rq.checkLogin()){
            if(rq.checkMember()){
                updateFavoriteTag(product);
            }
        }
        return ProductResponse.of(product);
    }

    public ProductListResponse getProducts(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
        return ProductListResponse.of(product);
    }

    private void updateFavoriteTag(Product product) {
        Member member = rq.getMember();
        if(member.getRole().equals(RoleType.MEMBER))
            favoriteTagService.updateTag(member, product, TagType.SHOW);
    }

    public Page<ProductListResponse> getProducts(int offset, String mainCategory, String subCategory) {

        ProductSearchCond cond = new ProductSearchCond(mainCategory, subCategory);
        PageRequest pageable = PageRequest.of(offset, 18);

        Page<ProductListResponse> listResponses = productRepository.findAllByCategoryId(cond, pageable);

        List<ProductListResponse> list = listResponses.getContent().stream()
                .map(product -> getProducts(product.getId()))
                .peek(product -> {
                    String updatedMainFile = product.getMainFile();
                    product.setMainFile(updatedMainFile);
                }).toList();

        return new PageImpl<>(list, pageable, listResponses.getTotalElements());
    }

    public Page<ProductListResponse> findAllByKeyword(String keyword, int offset) {

        ProductSearchCond cond = new ProductSearchCond(keyword);
        PageRequest pageable = PageRequest.of(offset, 18);

        Page<ProductListResponse> listResponses = productRepository.findAllByKeyword(cond, pageable);

        List<ProductListResponse> list = listResponses.getContent().stream()
                .map(product -> getProducts(product.getId()))
                .peek(product -> {
                    String updatedMainFile = product.getMainFile();
                    product.setMainFile(updatedMainFile);
                }).toList();

        return new PageImpl<>(list, pageable, listResponses.getTotalElements());
    }

    public Page<ProductListResponse> getAllProducts(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), 18);
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::convertToProductListResponse);
    }

    public List<Product> getRecommendProduct(Member member) {

        List<ProductByRecommended> tags = productRepository.findRecommendProduct(member.getUsername());
        Set<FavoriteTag> memberTags = member.getTags();


        //태그명에 따라 가지고 있는 product_id
        // : 멤버 태그명에 따라 해당 상품에 점수를 부여해야 하기 때문에
        Map<String, List<Long>> productIdListByTagName = new HashMap<>();

        //상품 id 중복을 없애기 위함
        //맴버의 태그명과 여러개가 겹쳐서 여러개의 추천상품이 반환되었을것 그 중복을 없애야 한다.
        Map<Long, ProductByRecommended> recommendProductByProductId = new HashMap<>();

        //같은 태그명을 가지고 있지만 제각각 상품을 가르키는 productId는 다를 것이다. 그래서 태그명 별로 어떤 상품들을 가르키는지 모아보자
        for (ProductByRecommended tag : tags) {
            if (!productIdListByTagName.containsKey(tag.getTagName()))
                productIdListByTagName.put(tag.getTagName(), new ArrayList<>());
            productIdListByTagName.get(tag.getTagName()).add(tag.getProductId());
        }

        //마찬가지로 같은 상품을 가르키고 있지만 태그명은 제각각일 것이다. 우리가 뽑아내길 원하는 것은 추천상품이다. 즉 같은 상품이 중복되면 안된다.
        //그래서 상품Id에 대한 중복을 없애서 하나에 몰아넣는 코드이다.
        for (ProductByRecommended response : tags) {
            if (recommendProductByProductId.containsKey(response.getProductId()))
                continue;
            recommendProductByProductId.put(response.getProductId(), response);
        }

        //실제로직! member 선호태그에는 점수가 있을 것이다.
        //그러니까  우리가 반환하려고 하는 추천상품이 점수가 몇점인지 갱신하는 코드이다.
        for (FavoriteTag memberTag : memberTags) {
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

        //Product 변환해서 리턴
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

    private ProductListResponse convertToProductListResponse(Product product) {
        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getBrand().getName(),
                product.getFile().getFileName(),
                product.getProductOption().getPrice(),
                product.getDiscountRate(),
                product.getDiscountedPrice()
        );
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));
    }

    public Product findByIdWithBrand(Long productId) {
        return productRepository.findByIdWithBrand(productId);
    }
}
