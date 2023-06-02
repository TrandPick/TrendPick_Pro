package project.trendpick_pro.domain.product.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
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
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.dto.response.ProductResponse;
import project.trendpick_pro.domain.product.entity.dto.response.RecommendProductExResponse;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tag.entity.Tag;
import project.trendpick_pro.domain.tag.entity.type.TagType;
import project.trendpick_pro.domain.tag.repository.TagRepository;
import project.trendpick_pro.domain.tag.service.TagService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final BrandRepository brandRepository;
    private final FileTranslator fileTranslator;
    private final TagRepository tagRepository;
    private final TagService tagService;

    @Transactional
    public ProductResponse register(ProductSaveRequest productSaveRequest) throws IOException {

        CommonFile mainFile = fileTranslator.translateFile(productSaveRequest.getMainFile());
        List<CommonFile> subFiles = fileTranslator.translateFileList(productSaveRequest.getSubFiles());

        for (CommonFile subFile : subFiles) {
            mainFile.connectFile(subFile);
        }

        List<Tag> tags = new ArrayList<>();  // 상품에 포함시킬 태크 선택하여 저장
        for (String tag : productSaveRequest.getTags()) {
            tags.add(tagRepository.findByName(tag).orElseThrow());
        }

        MainCategory mainCategory = mainCategoryRepository.findByName(productSaveRequest.getMainCategory());
        SubCategory subCategory = subCategoryRepository.findByName(productSaveRequest.getSubCategory());
        Brand brand = brandRepository.findByName(productSaveRequest.getBrand());

        Product product = Product.of(productSaveRequest, mainCategory, subCategory, brand, mainFile,tags);

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

    public ProductResponse show(Member member, Long product_id) {
        Product product = productRepository.findById(product_id).orElseThrow(null);// 임시. 나중에 테스트

        if(member != null){
            tagService.updateTag(member, product, TagType.SHOW);
        }

        return ProductResponse.of(product);
    }

    public Page<ProductListResponse> showAll(int offset, String mainCategory, String subCategory, Integer sortCode) {

        List<ProductResponse> responses = new ArrayList<>();

        ProductSearchCond cond = new ProductSearchCond(mainCategory, subCategory, sortCode);
        PageRequest pageable = PageRequest.of(offset, 18);

        return productRepository.findAllByCategoryId(cond, pageable);
    }

    @Transactional
    public List<RecommendProductExResponse> extractRecommendProductExResponse(Member member){
        List<RecommendProductExResponse> recommendProductExList = productRepository.findAllRecommendProductEx(member);
        List<Tag> memberTags = member.getTags();


        //태그명에 따라 가지고 있는 product_id
        // : 멤버 태그명에 따라 해당 상품에 점수를 부여해야 하기 때문에
        Map<String, List<Long>> productIdListByTagName = new HashMap<>();

        //상품 id 중복을 없애기 위함
        //맴버의 태그명과 여러개가 겹쳐서 여러개의 추천상품이 반환되었을것 그 중복을 없애야 한다.
        Map<Long, RecommendProductExResponse> recommendProductByProductId = new HashMap<>();

        for (RecommendProductExResponse response : recommendProductExList) {
            if(!productIdListByTagName.containsKey(response.getTagName()))
                productIdListByTagName.put(response.getTagName(), new ArrayList<Long>());

            productIdListByTagName.get(response.getTagName()).add(response.getProductId());
        }

        for (RecommendProductExResponse response : recommendProductExList) {
            if(recommendProductByProductId.containsKey(response.getProductId()))
                continue;

            recommendProductByProductId.put(response.getProductId(), response);
        }

        for (Tag memberTag : memberTags) {
            if(productIdListByTagName.containsKey(memberTag.getName())){
                List<Long> productIdList = productIdListByTagName.get(memberTag.getName());
                for (Long id : productIdList) {
                    recommendProductByProductId.get(id).plusTotalScore(memberTag.getScore());
                }
            }
        }

        return new ArrayList(recommendProductByProductId.values());
    }
}
