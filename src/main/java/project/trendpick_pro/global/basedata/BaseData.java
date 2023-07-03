package project.trendpick_pro.global.basedata;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.service.MainCategoryService;
import project.trendpick_pro.domain.category.service.SubCategoryService;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.recommend.service.RecommendService;
import project.trendpick_pro.domain.review.entity.Review;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewSaveRequest;
import project.trendpick_pro.domain.review.repository.ReviewRepository;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.global.basedata.tagname.entity.TagName;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@Profile({"dev", "test", "prod"})
public class BaseData {

    @Value("${tag}")
    private List<String> tags;
    @Value("${main-category}")
    private List<String> mainCategories;
    @Value("${top}")
    private List<String> tops;
    @Value("${outer}")
    private List<String> outers;
    @Value("${bottom}")
    private List<String> bottoms;
    @Value("${shoes}")
    private List<String> shoes;
    @Value("${bag}")
    private List<String> bags;
    @Value("${accessory}")
    private List<String> accessories;
    @Value("${brand}")
    private List<String> brands;
    @Value("${sizes.tops}")
    private List<String> sizeTops;
    @Value("${sizes.bottoms}")
    private List<String> sizeBottoms;
    @Value("${sizes.shoes}")
    private List<String> sizeShoes;
    @Value("${colors}")
    private List<String> colors;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Bean
    CommandLineRunner initData(
            TagNameService tagNameService,
            MemberService memberService,
            MainCategoryService mainCategoryService,
            SubCategoryService subCategoryService,
            BrandService brandService,
            RecommendService recommendService,
            ProductService productService,
            ProductRepository productRepository,
            ReviewRepository reviewRepository,
            CouponRepository couponRepository,
            StoreRepository storeRepository
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) {

                tagNameService.saveAll(tags);
                memberService.saveAll(makeBrandMembers(brands));
                mainCategoryService.saveAll(mainCategories);

                SaveAllSubCategories(mainCategoryService, subCategoryService);

                Map<String, String> accessKeyMap = new HashMap<>();
                accessKeyMap.put("accessKey", accessKey);
                accessKeyMap.put("secretKey", secretKey);
                accessKeyMap.put("bucket", bucket);

                int memberCount = 10;
                int productCount = 100;
                int reviewCount = 100;
                int couponCount = 50;
                String brandName = "polo";

                saveMembers(memberCount, tagNameService, memberService, recommendService);
                saveUniqueMembers(memberService, brandName);

                saveProducts(productCount, accessKeyMap, mainCategoryService, brandService, tagNameService, productRepository, brandName, sizeTops, sizeBottoms, sizeShoes, colors);
                updateRecommends(memberService, recommendService);

                saveReviews(reviewCount, productCount, accessKeyMap, memberService, productService ,reviewRepository);
                saveStoreCoupon(couponCount, storeRepository, couponRepository, brandService);

                log.info("BASE_DATA_SUCCESS");
            }
        };
    }

    private void saveMembers(int count, TagNameService tagNameService, MemberService memberService, RecommendService recommendService) {
        List<JoinForm> members = new ArrayList<>();
        for(int i=1; i<=count; i++){
            List<String> memberTags = new ArrayList<>();
            for (int j = 1; j <= (Math.random() * 10)+1; j++) {
                TagName tagName = tagNameService.findById((long)(Math.random() * 30) + 1L);
                memberTags.add(tagName.getName());
            }
            JoinForm member = JoinForm.builder()
                    .email("trendpick"+i+"@naver.com")
                    .password("12345")
                    .username("sooho"+i)
                    .phoneNumber("010-1234-1234")
                    .state("MEMBER")
                    .tags(memberTags)
                    .build();
            members.add(member);
        }
        List<Member> memberList = memberService.saveAll(members);
        for (Member member : memberList) {
            member.connectAddress("서울특별시 어디구 어디로 123");
            recommendService.select(member.getEmail());
        }
    }

    private List<JoinForm> makeBrandMembers(List<String> brandNames) {
        List<JoinForm> brandList = new ArrayList<>();
        for (String brandName : brandNames) {
            JoinForm brandAdmin = JoinForm.builder()
                    .email(brandName + "@naver.com")
                    .password("12345")
                    .username(brandName)
                    .phoneNumber("010-1234-1234")
                    .state("BRAND_ADMIN")
                    .brand(brandName)
                    .build();
            brandList.add(brandAdmin);
        }
        return brandList;
    }

    private void saveUniqueMembers(MemberService memberService, String brandName) {
        JoinForm admin = JoinForm.builder()
                .email("admin@naver.com")
                .password("12345")
                .username("admin")
                .phoneNumber("010-1234-1234")
                .state("ADMIN")
                .build();
        memberService.register(admin);

        JoinForm brandAdmin = JoinForm.builder()
                .email("brand@naver.com")
                .password("12345")
                .username("brand")
                .phoneNumber("010-1234-1234")
                .state("BRAND_ADMIN")
                .brand(brandName)
                .build();
        memberService.register(brandAdmin);

        JoinForm member = JoinForm.builder()
                .email("trendpick@naver.com")
                .password("12345")
                .username("sooho")
                .phoneNumber("010-1234-1234")
                .state("MEMBER")
                .tags(tags)
                .build();
        Member RsMember1 = memberService.register(member).getData();
        RsMember1.connectAddress("서울특별시 진짜 주인공 123");

        JoinForm member2 = JoinForm.builder()
                .email("hye_0000@naver.com")
                .password("12345")
                .username("hye0000")
                .phoneNumber("010-1234-1234")
                .state("MEMBER")
                .tags(List.of("오버핏청바지", "로맨틱룩"))
                .build();
        Member RsMember2 = memberService.register(member2).getData();
        RsMember2.connectAddress("서울특별시 진짜 주인공 123");
    }

    public void updateRecommends(MemberService memberService, RecommendService recommendService) {
        Optional<Member> findMember = memberService.findByEmail("trendpick@naver.com");
        findMember.ifPresent(member -> recommendService.select(member.getEmail()));
        findMember = memberService.findByEmail("hye_0000@naver.com");
        findMember.ifPresent(member -> recommendService.select(member.getEmail()));

    }

    private static void saveProducts(int count, Map<String, String> keys, MainCategoryService mainCategoryService, BrandService brandService, TagNameService tagNameService, ProductRepository productRepository, String brandName,
                                     List<String> sizeTops, List<String> sizeBottoms, List<String> sizeShoes, List<String> colors) {
        long result;
        List<Product> products = new ArrayList<>();
        for (int n = 1; n <= count; n++) {
            CommonFile commonFile = makeFiles(keys);
            result = (long) (Math.random() * 7);
            MainCategory mainCategory = mainCategoryService.findByBaseId(result + 1L);

            result = (long) (Math.random() * brandService.count());
            Brand brand = brandService.findById(result + 1L);
            Brand UniqueBrand = brandService.findByName(brandName);

            if (Math.random() < 0.1) {
                brand = UniqueBrand;
            }

            if (!Objects.equals(mainCategory.getName(), "추천")) {

                result = (int) (Math.random() * 6);
                List<SubCategory> subCategories = mainCategory.getSubCategories();
                SubCategory subCategory = subCategories.get((int) result);

                int stockRandom = (int) (Math.random() * 200)+ 100;
                int priceRandom = (int) (Math.random() * (250000 - 20000 + 1)) + 10000;

                List<String> inputColors = new ArrayList<>(colors); // colors 리스트 복사

                Collections.shuffle(inputColors); // 색상 리스트 섞기

                int numColors = (int) (Math.random() * 3) + 3; // 필요한 개수 설정
                if (numColors > inputColors.size()) {
                    numColors = inputColors.size(); // 필요한 개수가 실제 색상 리스트 크기보다 크다면 최대값으로 설정
                }

                List<String> selectedColors = inputColors.subList(0, numColors);

                List<String> inputSizes = new ArrayList<>();
                switch (mainCategory.getName()) {
                    case "상의", "아우터" -> {
                        int startIndex = (int) (Math.random() * 2);
                        int endIndex = startIndex + (int) (Math.random() * 4);
                        for (int i = startIndex; i < endIndex + 3; i++) {
                            inputSizes.add(sizeTops.get(i));
                        }
                    }
                    case "하의" -> {
                        int startIndex = (int) (Math.random() * 2);
                        int endIndex = startIndex + (int) (Math.random() * 8);
                        for (int i = startIndex; i < endIndex + 3; i++) {
                            inputSizes.add(sizeBottoms.get(i));
                        }
                    }
                    case "신발" -> {
                        int startIndex = (int) (Math.random() * 2);
                        int endIndex = startIndex + (int) (Math.random() * 8);
                        for (int i = startIndex; i < endIndex + 3; i++) {
                            inputSizes.add(sizeShoes.get(i));
                        }
                    }
                    default -> inputSizes.add("FREE");
                }

                ProductOptionSaveRequest request = ProductOptionSaveRequest.of(inputSizes, selectedColors, stockRandom, priceRandom);
                ProductOption productOption = ProductOption.of(request);

                Product product = Product
                        .builder()
                        .name(brand.getName() + " " + mainCategory.getName() + " " + subCategory.getName() + " 멋사입니다. ")
                        .description(brand.getName() + " " + mainCategory.getName() + " " + subCategory.getName() + " 멋사입니다. ")
                        .productOption(productOption)
                        .mainCategory(mainCategory)
                        .subCategory(subCategory)
                        .brand(brand)
                        .file(commonFile)
                        .build();
                Set<Tag> tags = new LinkedHashSet<>();
                for (int i = 1; i <= (Math.random() * 13)+5; i++) {
                    result = (long) (Math.random() * 30);
                    TagName tagName = tagNameService.findById(result + 1L);
                    tags.add(new Tag(tagName.getName()));
                }
                product.addTag(tags);
                products.add(product);
            }
        }
        productRepository.saveAll(products);
    }

    private void saveReviews(int count, int productCount, Map<String, String> keys, MemberService memberService, ProductService productService, ReviewRepository reviewRepository) {
        List<Review> reviews = new ArrayList<>();
        for(int i=1; i<=count; i++){
            CommonFile commonFile = makeFiles(keys);
            Product product = productService.findById((long) (Math.random() * (productCount/2))+1L);
            ReviewSaveRequest rr = ReviewSaveRequest.builder()
                    .title("리뷰입니다.")
                    .content("내용입니다")
                    .rating(5)
                    .build();
            reviews.add(Review.of(rr, memberService.findByEmail("trendpick@naver.com").get(), product, commonFile));
        }
        reviewRepository.saveAll(reviews);
    }
    private void saveStoreCoupon(int couponCount, StoreRepository storeRepository, CouponRepository couponRepository, BrandService brandService){
        List<Coupon> coupons = new ArrayList<>();
        for(int i=1; i<couponCount; i++){
            int limitCount = (int) (Math.random() * 200)+ 100;
            int limitIssueDate = (int) (Math.random() * 360) + 1;
            int minimumPurchaseAmount = (int) (Math.random() * 50000) + 1000;
            int discountPercent = (int) (Math.random() * 90) + 5;
            int issueAfterDate = (int) (Math.random() * 20) + 7;
            long result = (long) (Math.random() * 7) + 1L;
            Store store = storeRepository.findById(result + 1L).get();
            StoreCouponSaveRequest storeCouponSaveRequest = new StoreCouponSaveRequest(
                    "쿠폰" + i,
                    limitCount,
                    limitIssueDate,
                    minimumPurchaseAmount,
                    discountPercent,
                    "ISSUE_AFTER_DATE",
                    null,
                    null,
                    issueAfterDate
            );
            coupons.add(Coupon.generate(store, storeCouponSaveRequest));
        }
        couponRepository.saveAll(coupons);
    }

    private void SaveAllSubCategories(MainCategoryService mainCategoryService, SubCategoryService subCategoryService) {
        subCategoryService.saveAll(tops, mainCategoryService.findByName("상의"));
        subCategoryService.saveAll(outers, mainCategoryService.findByName("아우터"));
        subCategoryService.saveAll(bottoms, mainCategoryService.findByName("하의"));
        subCategoryService.saveAll(shoes, mainCategoryService.findByName("신발"));
        subCategoryService.saveAll(bags, mainCategoryService.findByName("가방"));
        subCategoryService.saveAll(accessories, mainCategoryService.findByName("악세서리"));
    }

    public static CommonFile makeFiles(Map<String, String> keys) {
        AmazonS3 s3Client = createNcpS3Client(keys.get("accessKey"), keys.get("secretKey"));

        List<String> filenames = listS3ObjectKeys(s3Client, keys.get("bucket"));

        CommonFile mainFile = CommonFile.builder()
                .fileName(selectRandomFilePath(filenames))
                .build();
        for (int i = 0; i < (int) (Math.random() * 6) + 2; i++) {
            mainFile.connectFile(CommonFile.builder()
                    .fileName(selectRandomFilePath(filenames))
                    .build());
        }
        return mainFile;
    }

    private static AmazonS3 createNcpS3Client(String accessKey, String secretKey) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

        ClientConfiguration clientConfig = new ClientConfiguration();

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration("https://kr.object.ncloudstorage.com", Regions.AP_NORTHEAST_2.getName()))
                .build();
    }

    private static List<String> listS3ObjectKeys(AmazonS3 s3Client, String bucketName) {
        ObjectListing objectListing = s3Client.listObjects(bucketName);

        return objectListing.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    private static String selectRandomFilePath(List<String> filePaths) {
        Random random = new Random();
        String path = filePaths.get(random.nextInt(filePaths.size()));
        while (path.equals("trendpick_logo.png")) {
            path = filePaths.get(random.nextInt(filePaths.size()));
        }
        return path;
    }
}