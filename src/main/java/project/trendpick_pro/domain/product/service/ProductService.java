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
import java.util.stream.Collectors;

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


        //일단 추천상품으로 뽑힌것들 중복제거 (한 상품이 여러 태그에 포함되있을 수 있다.)
        Map<String, List<Long>> map = new HashMap<>();
        Map<Long, RecommendProductExResponse> map2 = new HashMap<>();

        for (RecommendProductExResponse response : recommendProductExList) {
            logger.info(response.getTagName());
            if(!map.containsKey(response.getTagName()))
                map.put(response.getTagName(), new ArrayList<Long>());

            map.get(response.getTagName()).add(response.getProductId());
        }

        for (RecommendProductExResponse response : recommendProductExList) {
            if(map2.containsKey(response.getProductId()))
                continue;

            map2.put(response.getProductId(), response);
        }
        logger.info("점수 상승=============================================================");
        for (Tag memberTag : memberTags) {
            if(map.containsKey(memberTag.getName())){
                List<Long> productIdList = map.get(memberTag.getName());
                for (Long id : productIdList) {
                    logger.info("---------------------------------------");
                    logger.info(String.valueOf(map2.get(id).getTotalScore()));
                    map2.get(id).plusTotalScore(memberTag.getScore());
                    logger.info(String.valueOf(map2.get(id).getTotalScore()));
                    logger.info("---------------------------------------");
                }
            }
        }
        logger.info("점수 상승=============================================================");

        return new ArrayList(map2.values());
    }



}
